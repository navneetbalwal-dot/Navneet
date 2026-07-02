package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Attempt
import com.example.data.model.Question
import com.example.data.model.Subject
import com.example.data.model.ExamType
import com.example.data.repository.ExamRepository
import com.example.network.GeminiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExamRepository

    // Database Flows
    val allQuestions: StateFlow<List<Question>>
    val bookmarkedQuestions: StateFlow<List<Question>>
    val allAttempts: StateFlow<List<Attempt>>

    // UI filters
    private val _selectedExam = MutableStateFlow<String>("ALL")
    val selectedExam = _selectedExam.asStateFlow()

    private val _selectedSubject = MutableStateFlow<String>("ALL")
    val selectedSubject = _selectedSubject.asStateFlow()

    // Filtered questions for browsing
    val filteredQuestions: StateFlow<List<Question>>

    // Active Quiz States
    private val _quizQuestions = MutableStateFlow<List<Question>>(emptyList())
    val quizQuestions = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    // Maps questionId -> Selected Option ("A", "B", "C", "D")
    private val _selectedAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val selectedAnswers = _selectedAnswers.asStateFlow()

    // Maps questionId -> Checked state (True if user has locked in the answer for instant review)
    private val _checkedAnswers = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val checkedAnswers = _checkedAnswers.asStateFlow()

    private val _quizTitle = MutableStateFlow("Practice Quiz")
    val quizTitle = _quizTitle.asStateFlow()

    private val _quizSeconds = MutableStateFlow(0)
    val quizSeconds = _quizSeconds.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted = _quizCompleted.asStateFlow()

    // Result calculations after submission
    private val _correctCount = MutableStateFlow(0)
    val correctCount = _correctCount.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount = _totalCount.asStateFlow()

    // Detailed explanation from Gemini for active question
    private val _aiExplanation = MutableStateFlow<String?>(null)
    val aiExplanation = _aiExplanation.asStateFlow()

    private val _isAiExplaining = MutableStateFlow(false)
    val isAiExplaining = _isAiExplaining.asStateFlow()

    // General AI Tutor Chat History (List of Pair(UserMessage, TutorResponse))
    private val _tutorHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val tutorHistory = _tutorHistory.asStateFlow()

    private val _isTutorLoading = MutableStateFlow(false)
    val isTutorLoading = _isTutorLoading.asStateFlow()

    private val _tutorInput = MutableStateFlow("")
    val tutorInput = _tutorInput.asStateFlow()

    private var timerJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ExamRepository(database.examDao())

        allQuestions = repository.allQuestions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        bookmarkedQuestions = repository.bookmarkedQuestions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allAttempts = repository.allAttempts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed DB on start
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }

        // Setup filter combining logic
        filteredQuestions = combine(
            allQuestions,
            _selectedExam,
            _selectedSubject
        ) { questions, exam, subject ->
            questions.filter { q ->
                (exam == "ALL" || q.examType == exam) &&
                (subject == "ALL" || q.subject == subject)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // --- Filter Setters ---
    fun setExamFilter(exam: String) {
        _selectedExam.value = exam
    }

    fun setSubjectFilter(subject: String) {
        _selectedSubject.value = subject
    }

    // --- Quiz Operations ---
    fun startNewQuiz(questions: List<Question>, title: String) {
        stopTimer()
        _quizQuestions.value = questions.shuffled().take(10) // Limit to max 10 for a tight focused session
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _checkedAnswers.value = emptyMap()
        _quizTitle.value = title
        _quizSeconds.value = 0
        _quizCompleted.value = false
        _correctCount.value = 0
        _totalCount.value = _quizQuestions.value.size
        _aiExplanation.value = null
        _isAiExplaining.value = false

        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _quizSeconds.value += 1
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun selectOption(questionId: Int, option: String) {
        if (_quizCompleted.value) return // Can't select after submit
        val updated = _selectedAnswers.value.toMutableMap()
        updated[questionId] = option
        _selectedAnswers.value = updated
    }

    fun lockAnswer(questionId: Int) {
        val updated = _checkedAnswers.value.toMutableMap()
        updated[questionId] = true
        _checkedAnswers.value = updated

        // Load AI explanation in the background as soon as they check, so it's ready when they look
        val currentQ = _quizQuestions.value.find { it.id == questionId }
        val selected = _selectedAnswers.value[questionId] ?: ""
        if (currentQ != null && selected.isNotEmpty()) {
            loadAiExplanation(currentQ, selected)
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _quizQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
            _aiExplanation.value = null
            // Preload AI explanation if already answered
            val nextQ = _quizQuestions.value[_currentQuestionIndex.value]
            val selected = _selectedAnswers.value[nextQ.id]
            val isChecked = _checkedAnswers.value[nextQ.id] == true
            if (selected != null && isChecked) {
                loadAiExplanation(nextQ, selected)
            }
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
            _aiExplanation.value = null
            val prevQ = _quizQuestions.value[_currentQuestionIndex.value]
            val selected = _selectedAnswers.value[prevQ.id]
            val isChecked = _checkedAnswers.value[prevQ.id] == true
            if (selected != null && isChecked) {
                loadAiExplanation(prevQ, selected)
            }
        }
    }

    fun toggleBookmark(questionId: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId, isBookmarked)
        }
    }

    fun loadAiExplanation(question: Question, selected: String) {
        _aiExplanation.value = null
        _isAiExplaining.value = true
        viewModelScope.launch {
            val explanation = GeminiClient.getAiExplanation(
                questionText = question.questionText,
                subject = question.subject,
                selectedOption = selected,
                correctOption = question.correctAnswer
            )
            _aiExplanation.value = explanation
            _isAiExplaining.value = false
        }
    }

    fun submitFullQuiz() {
        if (_quizCompleted.value) return
        stopTimer()
        _quizCompleted.value = true

        // Calculate correct count
        var correct = 0
        _quizQuestions.value.forEach { q ->
            val sel = _selectedAnswers.value[q.id]
            if (sel == q.correctAnswer) {
                correct++
            }
        }
        _correctCount.value = correct

        // Save Attempt to Room
        viewModelScope.launch {
            val attempt = Attempt(
                title = _quizTitle.value,
                correctCount = correct,
                totalCount = _totalCount.value,
                durationSeconds = _quizSeconds.value,
                subject = if (_selectedSubject.value == "ALL") null else _selectedSubject.value
            )
            repository.insertAttempt(attempt)
        }
    }

    // --- General Tutor Chat ---
    fun updateTutorInput(text: String) {
        _tutorInput.value = text
    }

    fun sendTutorQuery() {
        val query = _tutorInput.value.trim()
        if (query.isEmpty() || _isTutorLoading.value) return

        _tutorInput.value = ""
        _isTutorLoading.value = true

        // Append user query to list
        val currentHistory = _tutorHistory.value.toMutableList()
        currentHistory.add(Pair(query, "Thinking..."))
        _tutorHistory.value = currentHistory

        viewModelScope.launch {
            val historyWithoutPlaceholder = currentHistory.dropLast(1)
            val response = GeminiClient.askTutorGeneralQuery(query, historyWithoutPlaceholder)
            
            // Replace placeholder with actual response
            val updatedHistory = currentHistory.toMutableList()
            updatedHistory[updatedHistory.lastIndex] = Pair(query, response)
            _tutorHistory.value = updatedHistory
            _isTutorLoading.value = false
        }
    }

    fun clearChat() {
        _tutorHistory.value = emptyList()
    }

    fun selectTutorSuggestedPrompt(prompt: String) {
        _tutorInput.value = prompt
        sendTutorQuery()
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}

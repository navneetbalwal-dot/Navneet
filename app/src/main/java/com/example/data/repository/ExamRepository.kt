package com.example.data.repository

import android.util.Log
import com.example.data.dao.ExamDao
import com.example.data.database.InitialQuestions
import com.example.data.model.Attempt
import com.example.data.model.Bookmark
import com.example.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExamRepository(private val examDao: ExamDao) {

    val allQuestions: Flow<List<Question>> = examDao.getAllQuestions()
    val bookmarkedQuestions: Flow<List<Question>> = examDao.getBookmarkedQuestions()
    val allAttempts: Flow<List<Attempt>> = examDao.getAllAttempts()

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        try {
            val count = examDao.getQuestionCount()
            if (count == 0) {
                Log.d("ExamRepository", "Seeding database with ${InitialQuestions.list.size} questions")
                examDao.insertQuestions(InitialQuestions.list)
            } else {
                Log.d("ExamRepository", "Database already seeded with $count questions")
            }
        } catch (e: Exception) {
            Log.e("ExamRepository", "Error seeding database: ${e.message}", e)
        }
    }

    fun getQuestionsByExam(examType: String): Flow<List<Question>> = examDao.getQuestionsByExam(examType)

    fun getQuestionsBySubject(subject: String): Flow<List<Question>> = examDao.getQuestionsBySubject(subject)

    fun getQuestions(examType: String, subject: String): Flow<List<Question>> = examDao.getQuestions(examType, subject)

    suspend fun getQuestionById(id: Int): Question? = examDao.getQuestionById(id)

    suspend fun toggleBookmark(questionId: Int, shouldBookmark: Boolean) = withContext(Dispatchers.IO) {
        if (shouldBookmark) {
            examDao.insertBookmark(Bookmark(questionId))
        } else {
            examDao.deleteBookmark(questionId)
        }
    }

    fun isBookmarked(questionId: Int): Flow<Boolean> = examDao.isBookmarked(questionId)

    suspend fun insertAttempt(attempt: Attempt) = withContext(Dispatchers.IO) {
        examDao.insertAttempt(attempt)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        examDao.clearAllAttempts()
    }
}

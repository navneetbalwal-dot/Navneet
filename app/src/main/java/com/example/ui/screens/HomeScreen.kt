package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Attempt
import com.example.data.model.ExamType
import com.example.data.model.Question
import com.example.data.model.Subject
import com.example.ui.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ExamViewModel,
    onNavigateToQuiz: () -> Unit,
    onNavigateToTutor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.allQuestions.collectAsState()
    val attempts by viewModel.allAttempts.collectAsState()
    val bookmarks by viewModel.bookmarkedQuestions.collectAsState()

    var selectedExamFilter by remember { mutableStateOf<ExamType?>(null) }

    // Calculate dynamic stats
    val totalSolved = attempts.sumOf { it.totalCount }
    val correctSolved = attempts.sumOf { it.correctCount }
    val accuracy = if (totalSolved > 0) (correctSolved * 100) / totalSolved else 0
    val streak = if (attempts.isNotEmpty()) 3 else 1 // Simulated/persistent daily streak

    // Filter exams/mock tests based on selection
    val filteredExams = if (selectedExamFilter == null) {
        ExamType.values().toList()
    } else {
        ExamType.values().filter { it == selectedExamFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 0. Brand Header Row (Geometric Balance style)
        item {
            BrandHeaderRow()
        }

        // 1. Exam Category Nav Chips Row
        item {
            FilterChipsRow(
                selectedFilter = selectedExamFilter,
                onFilterSelected = { selectedExamFilter = it }
            )
        }

        // 2. Welcome Banner
        item {
            HeaderBanner(streak = streak)
        }

        // 3. Stats Section
        item {
            StatsSection(
                totalSolved = totalSolved,
                accuracy = accuracy,
                bookmarksCount = bookmarks.size
            )
        }

        // 3.5 Preparation Score Card (Geometric Balance style)
        item {
            PreparationScoreCard(accuracy = accuracy)
        }

        // 4. Exam Prep Hub (Browse by Exams)
        item {
            Text(
                text = "Target Exam Mock Tests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredExams) { exam ->
                    ExamCard(
                        exam = exam,
                        onClick = {
                            val examQuestions = questions.filter { it.examType == exam.name }
                            val quizList = if (examQuestions.isEmpty()) questions else examQuestions
                            viewModel.startNewQuiz(quizList, "Full ${exam.displayName} PYQ Practice")
                            onNavigateToQuiz()
                        }
                    )
                }
            }
        }

        // 5. Subject Wise Practice (Geometric Balance 2-Column Grid style)
        item {
            Text(
                text = "Practice by Subject",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val chunks = Subject.values().toList().chunked(2)
                chunks.forEach { rowSubjects ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowSubjects.forEach { subject ->
                            SubjectGridItem(
                                subject = subject,
                                questionCount = questions.count { it.subject == subject.name },
                                onClick = {
                                    val subQuestions = questions.filter { it.subject == subject.name }
                                    val quizList = if (subQuestions.isEmpty()) questions else subQuestions
                                    viewModel.startNewQuiz(quizList, "${subject.displayName} Quiz")
                                    onNavigateToQuiz()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowSubjects.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 6. Daily Mixed Mock Challenge (Geometric Balance style card)
        item {
            DailyAiChallengeCard(
                onClick = {
                    viewModel.startNewQuiz(questions, "Daily Mixed Mock Challenge")
                    onNavigateToQuiz()
                }
            )
        }

        // 7. Rapid AI Help Card
        item {
            AiQuickHelpCard(onNavigateToTutor = onNavigateToTutor)
        }
    }
}

@Composable
fun HeaderBanner(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome Back, Aspirant!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ready to boost your SSC score today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            // Streak counter
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Color(0xFFFF9800)
                    )
                    Text(
                        text = "$streak Days",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun StatsSection(totalSolved: Int, accuracy: Int, bookmarksCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            title = "Solved",
            value = "$totalSolved",
            icon = Icons.Outlined.CheckCircle,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Accuracy",
            value = "$accuracy%",
            icon = Icons.Outlined.Psychology,
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Bookmarks",
            value = "$bookmarksCount",
            icon = Icons.Outlined.Book,
            color = Color(0xFFE91E63),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ExamCard(
    exam: ExamType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
            .testTag("exam_card_${exam.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = exam.displayName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = exam.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2
            )
        }
    }
}

@Composable
fun SubjectRowItem(
    subject: Subject,
    questionCount: Int,
    onClick: () -> Unit
) {
    val icon = when (subject) {
        Subject.QUANT -> Icons.Default.Functions
        Subject.REASONING -> Icons.Default.Extension
        Subject.ENGLISH -> Icons.Default.Translate
        Subject.GK -> Icons.Default.Language
    }

    val iconBgColor = when (subject) {
        Subject.QUANT -> Color(0xFFE3F2FD)
        Subject.REASONING -> Color(0xFFEDE7F6)
        Subject.ENGLISH -> Color(0xFFF1F8E9)
        Subject.GK -> Color(0xFFFFF3E0)
    }

    val iconColor = when (subject) {
        Subject.QUANT -> Color(0xFF1E88E5)
        Subject.REASONING -> Color(0xFF5E35B1)
        Subject.ENGLISH -> Color(0xFF7CB342)
        Subject.GK -> Color(0xFFFB8C00)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("subject_card_${subject.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$questionCount PYQs loaded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Practice",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DailyAiChallengeCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_ai_challenge")
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp), // Exact rounded-[28px]
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6750A4) // Exact bg-[#6750A4]
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Background overlapping circle representing "Geometric Balance"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 30.dp, y = 30.dp)
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD0BCFF).copy(alpha = 0.2f)) // Exact bg-[#D0BCFF] rounded-full opacity-20
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pill/Badge
                Badge(
                    containerColor = Color(0xFFFFD8E4), // Exact bg-[#FFD8E4]
                    contentColor = Color(0xFF31111D) // Exact text-[#31111D]
                ) {
                    Text(
                        text = "DAILY CHALLENGE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    )
                }

                Column {
                    Text(
                        text = "SSC CGL 2023",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light, fontSize = 24.sp), // Exact font-light mt-2 text-2xl
                        color = Color.White
                    )
                    Text(
                        text = "Shift 1 - Full Mock Test",
                        style = MaterialTheme.typography.bodyMedium, // Exact text-sm mt-1
                        color = Color(0xFFE6E1E5) // Exact text-[#E6E1E5]
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Button styled exactly like Tailwind button
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD0BCFF), // Exact bg-[#D0BCFF]
                            contentColor = Color(0xFF381E72) // Exact text-[#381E72]
                        ),
                        shape = RoundedCornerShape(20.dp), // Exact rounded-full
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Start Practice",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Column {
                        Text(
                            text = "100 Qs",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "60 Mins",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE6E1E5)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiQuickHelpCard(onNavigateToTutor: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToTutor() }
            .testTag("ai_coach_shortcut"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(36.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Stuck on a difficult concept?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Chat with our Gemini-powered AI Tutor anytime for customized shortcuts and formulas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun BrandHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)) // rounded-xl
                    .background(Color(0xFF6750A4)), // bg-[#6750A4]
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Column {
                Text(
                    text = "ExamPrep Pro",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "SSC & COMPETITIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFEADDFF)), // bg-[#EADDFF]
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                tint = Color(0xFF21005D), // text-[#21005D]
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun FilterChipsRow(
    selectedFilter: ExamType?,
    onFilterSelected: (ExamType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Show "All" chip
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onFilterSelected(null) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedFilter == null) Color(0xFFE8DEF8) else Color(0xFFF3EDF7)
            ),
            border = if (selectedFilter == null) null else BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ALL EXAMS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    color = if (selectedFilter == null) Color(0xFF1D192B) else Color(0xFF49454F)
                )
            }
        }

        // SSC CGL
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onFilterSelected(ExamType.CGL) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedFilter == ExamType.CGL) Color(0xFFE8DEF8) else Color(0xFFF3EDF7)
            ),
            border = if (selectedFilter == ExamType.CGL) null else BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SSC CGL",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    color = if (selectedFilter == ExamType.CGL) Color(0xFF1D192B) else Color(0xFF49454F)
                )
            }
        }

        // SSC CHSL
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onFilterSelected(ExamType.CHSL) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedFilter == ExamType.CHSL) Color(0xFFE8DEF8) else Color(0xFFF3EDF7)
            ),
            border = if (selectedFilter == ExamType.CHSL) null else BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CHSL",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    color = if (selectedFilter == ExamType.CHSL) Color(0xFF1D192B) else Color(0xFF49454F)
                )
            }
        }

        // SSC MTS
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onFilterSelected(ExamType.MTS) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedFilter == ExamType.MTS) Color(0xFFE8DEF8) else Color(0xFFF3EDF7)
            ),
            border = if (selectedFilter == ExamType.MTS) null else BorderStroke(1.dp, Color(0xFFCAC4D0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MTS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    color = if (selectedFilter == ExamType.MTS) Color(0xFF1D192B) else Color(0xFF49454F)
                )
            }
        }
    }
}

@Composable
fun PreparationScoreCard(accuracy: Int) {
    val score = 300 + (accuracy * 5)
    val percentageImprovement = if (accuracy > 0) 12 else 0
    val topPercent = if (accuracy >= 80) "Top 2%" else if (accuracy >= 60) "Top 5%" else "Top 15%"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFE7E0EC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3EDF7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = Color(0xFF6750A4),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Preparation Score",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = "Improved by $percentageImprovement% this week",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF49454F)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF6750A4)
                )
                Text(
                    text = topPercent,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF49454F)
                )
            }
        }
    }
}

@Composable
fun SubjectGridItem(
    subject: Subject,
    questionCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (subject) {
        Subject.QUANT -> Icons.Default.Functions
        Subject.REASONING -> Icons.Default.Extension
        Subject.ENGLISH -> Icons.Default.Translate
        Subject.GK -> Icons.Default.Language
    }

    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("subject_card_${subject.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F2FA)
        ),
        border = BorderStroke(1.dp, Color(0xFFE7E0EC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6750A4),
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = subject.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1D1B20),
                    maxLines = 1
                )
                Text(
                    text = "$questionCount+ Questions",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF49454F)
                )
            }
        }
    }
}

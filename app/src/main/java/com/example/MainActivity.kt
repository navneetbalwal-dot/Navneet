package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ExamViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: ExamViewModel = viewModel()
                
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                // Immersive Quiz Experience: Hide bottom bar inside active quiz sessions
                val showBottomBar = currentRoute != "quiz"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = {
                                        if (currentRoute != "home") {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "ai_tutor",
                                    onClick = {
                                        if (currentRoute != "ai_tutor") {
                                            navController.navigate("ai_tutor") {
                                                popUpTo("home")
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Tutor") },
                                    label = { Text("AI Coach") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "bookmarks",
                                    onClick = {
                                        if (currentRoute != "bookmarks") {
                                            navController.navigate("bookmarks") {
                                                popUpTo("home")
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks") },
                                    label = { Text("Bookmarks") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "history",
                                    onClick = {
                                        if (currentRoute != "history") {
                                            navController.navigate("history") {
                                                popUpTo("home")
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                                    label = { Text("History") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToQuiz = { navController.navigate("quiz") },
                                onNavigateToTutor = { navController.navigate("ai_tutor") }
                            )
                        }
                        
                        composable("quiz") {
                            QuizScreen(
                                viewModel = viewModel,
                                onNavigateHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("ai_tutor") {
                            AiTutorScreen(
                                viewModel = viewModel
                            )
                        }
                        
                        composable("bookmarks") {
                            BookmarksScreen(
                                viewModel = viewModel
                            )
                        }
                        
                        composable("history") {
                            HistoryScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

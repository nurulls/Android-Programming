package com.example.dailymooduas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dailymooduas.data.model.GoogleAuthUiClient
import com.example.dailymooduas.presentation.sign_in.SignInScreen
import com.example.dailymooduas.presentation.sign_in.SignInViewModel
import com.example.dailymooduas.presentation.mood.*
import com.example.dailymooduas.presentation.profile.ProfileScreen
import com.example.dailymooduas.ui.theme.DailyMoodUASTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy { GoogleAuthUiClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyMoodUASTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val moodViewModel = viewModel<MoodViewModel>()

                    NavHost(navController = navController, startDestination = "sign_in") {
                        // Sign In Screen
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("mood_list") {
                                        popUpTo("sign_in") { inclusive = true }
                                    }
                                }
                            }

                            LaunchedEffect(state.isSignInSuccessfull) {
                                if (state.isSignInSuccessfull) {
                                    navController.navigate("mood_list") {
                                        popUpTo("sign_in") { inclusive = true }
                                    }
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val result = googleAuthUiClient.signIn()
                                        viewModel.onSignInResult(result)
                                    }
                                }
                            )
                        }

                        // Mood List Screen
                        composable("mood_list") {
                            Log.d("MainActivity", "Navigated to mood_list")
                            MoodScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                viewModel = moodViewModel,
                                onNavigateToProfile = {
                                    Log.d("MainActivity", "Navigating to profile")
                                    navController.navigate("profile")
                                },
                                onNavigateToEdit = { moodId ->
                                    Log.d("MainActivity", "Navigating to edit_mood/$moodId")
                                    navController.navigate("edit_mood/$moodId")
                                }
                            )
                        }

                        // Profile Screen
                        composable("profile") {
                            Log.d("MainActivity", "Navigated to profile")
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onBack = {
                                    Log.d("MainActivity", "Profile - Back clicked")
                                    navController.popBackStack()
                                },
                                onSignOut = {
                                    Log.d("MainActivity", "Profile - Sign out clicked")
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        navController.navigate("sign_in") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        // Edit Mood Screen
                        composable(
                            route = "edit_mood/{moodId}",
                            arguments = listOf(navArgument("moodId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val moodId = backStackEntry.arguments?.getString("moodId") ?: ""
                            Log.d("MainActivity", "Navigated to edit_mood with id: $moodId")
                            val moods by moodViewModel.moods.collectAsState()
                            val mood = moods.find { it.id == moodId }
                            val userId = googleAuthUiClient.getSignedInUser()?.userId ?: ""

                            mood?.let {
                                EditMoodScreen(
                                    mood = it,
                                    onSave = { newMoodType, newNote ->
                                        moodViewModel.updateMood(userId, moodId, newMoodType, newNote)
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
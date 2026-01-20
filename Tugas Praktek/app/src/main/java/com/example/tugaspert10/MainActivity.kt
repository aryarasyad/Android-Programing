package com.example.tugaspert10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tugaspert10.data.model.GoogleAuthUIClient
import com.example.tugaspert10.presentation.sign_in.SignInScreen
import com.example.tugaspert10.presentation.sign_in.SignInViewModel
import com.example.tugaspert10.presentation.todo.EditTodoScreen
import com.example.tugaspert10.presentation.todo.TodoScreen
import com.example.tugaspert10.presentation.todo.TodoViewModel
import com.example.tugaspert10.ui.theme.TugasPert10Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TugasPert10Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(googleAuthUIClient)
                }
            }
        }
    }

    @Composable
    private fun AppNavigation(googleAuthUIClient: GoogleAuthUIClient) {
        val navController = rememberNavController()
        val todoViewModel: TodoViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = "sign_in"
        ) {
            // SCREEN SIGN IN
            composable("sign_in") {
                val signInViewModel: SignInViewModel = viewModel()
                val state by signInViewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    if (googleAuthUIClient.getSignedInUser() != null) {
                        navController.navigate("todo_list") {
                            popUpTo("sign_in") { inclusive = true }
                        }
                    }
                }

                LaunchedEffect(state.isSignInSuccessfull) {
                    if (state.isSignInSuccessfull) {
                        navController.navigate("todo_list") {
                            popUpTo("sign_in") { inclusive = true }
                        }
                        signInViewModel.resetState()
                    }
                }

                SignInScreen(
                    state = state,
                    onSignInClick = {
                        lifecycleScope.launch {
                            val result = googleAuthUIClient.signIn()
                            signInViewModel.onSignInResult(result)
                        }
                    }
                )
            }

            // SCREEN TODO LIST
            composable("todo_list") {
                TodoScreen(
                    userData = googleAuthUIClient.getSignedInUser(),
                    viewModel = todoViewModel,
                    onSignOut = {
                        lifecycleScope.launch {
                            googleAuthUIClient.signOut()
                            navController.navigate("sign_in") {
                                popUpTo("todo_list") { inclusive = true }
                            }
                        }
                    },
                    onNavigateToEdit = { todoId ->
                        navController.navigate("edit_todo/$todoId")
                    }
                )
            }

            // SCREEN EDIT TODO
            composable(
                route = "edit_todo/{todoId}",
                arguments = listOf(navArgument("todoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val todoId = backStackEntry.arguments?.getString("todoId") ?: ""
                val todos by todoViewModel.todos.collectAsStateWithLifecycle()
                val todo = todos.find { it.id == todoId }
                val userId = googleAuthUIClient.getSignedInUser()?.userId ?: ""

                todo?.let {
                    EditTodoScreen(
                        todo = it,
                        onSave = { newTitle, newPriority ->
                            // Update title
                            todoViewModel.updateTitle(userId, todoId, newTitle)
                            // Update priority
                            todoViewModel.updatePriority(userId, todoId, newPriority)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
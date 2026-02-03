package com.example.projectuas.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.projectuas.data.GoogleAuthUIClient
import com.example.projectuas.presentation.journal.DetailJournalScreen
import com.example.projectuas.presentation.journal.EditJournalScreen
import com.example.projectuas.presentation.journal.JournalScreen
import com.example.projectuas.presentation.journal.JournalViewModel
import com.example.projectuas.presentation.sign_in.SignInScreen
import com.example.projectuas.presentation.sign_in.SignInViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    googleAuthUIClient: GoogleAuthUIClient
) {
    val navController = rememberNavController()
    val journalViewModel: JournalViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "sign_in"
    ) {

        // 🔐 SIGN IN
        composable("sign_in") {
            val signInViewModel: SignInViewModel = viewModel()
            val state by signInViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                if (googleAuthUIClient.getSignedInUser() != null) {
                    navController.navigate("journal") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                }
            }

            LaunchedEffect(state.isSignInSuccessfull) {
                if (state.isSignInSuccessfull) {
                    navController.navigate("journal") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                    signInViewModel.resetState()
                }
            }

            SignInScreen(
                state = state,
                onSignInClick = {
                    coroutineScope.launch {
                        val result = googleAuthUIClient.signIn()
                        signInViewModel.onSignInResult(result)
                    }
                }
            )
        }

        // 📘 JOURNAL LIST
        composable("journal") {
            val journals by journalViewModel.journals.collectAsStateWithLifecycle()

            // Ambil data user yang sedang aktif
            val userData = remember { googleAuthUIClient.getSignedInUser() }

            // 🔥 SETIAP KALI USER LOGIN (UserId berubah), TRIGGER OBSERVASI DATA
            LaunchedEffect(userData?.userId) {
                userData?.userId?.let { id ->
                    journalViewModel.observeJournals(id)
                }
            }

            JournalScreen(
                userData = userData,
                journals = journals,
                onAddJournal = { navController.navigate("add_journal") },
                onEditJournal = { journal -> navController.navigate("edit_journal/${journal.id}") },
                onOpenDetail = { journal -> navController.navigate("detail/${journal.id}") },
                onDeleteJournal = { journal -> journalViewModel.deleteJournal(journal.id) },
                onSignOut = {
                    coroutineScope.launch {
                        googleAuthUIClient.signOut()
                        journalViewModel.clearDataOnSignOut() // Bersihkan data di VM
                        navController.navigate("sign_in") {
                            popUpTo("journal") { inclusive = true }
                        }
                    }
                }
            )

        }

        // ➕ ADD JOURNAL (Ini yang tadi hilang sehingga menyebabkan crash)
        composable("add_journal") {
            val user = remember { googleAuthUIClient.getSignedInUser() }

            EditJournalScreen(
                journal = null,
                onBack = { navController.popBackStack() },
                onSave = { title, content, mood ->
                    user?.userId?.let { userId ->
                        journalViewModel.addJournal(userId, title, content, mood)
                        navController.popBackStack()
                    }
                }
            )
        }

        // ✏️ EDIT JOURNAL
        composable(
            route = "edit_journal/{journalId}",
            arguments = listOf(navArgument("journalId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getString("journalId")
            val journals by journalViewModel.journals.collectAsStateWithLifecycle()
            val journal = journals.firstOrNull { it.id == journalId }

            EditJournalScreen(
                journal = journal,
                onBack = { navController.popBackStack() },
                onSave = { title, content, mood ->
                    journal?.let {
                        journalViewModel.updateJournal(it.id, title, content, mood)
                        navController.popBackStack()
                    }
                }
            )
        }

        // 📖 DETAIL JOURNAL
        composable(
            route = "detail/{journalId}",
            arguments = listOf(navArgument("journalId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getString("journalId")
            val journals by journalViewModel.journals.collectAsStateWithLifecycle()
            val journal = journals.firstOrNull { it.id == journalId }

            if (journal == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            } else {
                DetailJournalScreen(
                    journal = journal,
                    onBack = { navController.popBackStack() },
                    onEdit = {
                        navController.navigate("edit_journal/${journal.id}")
                    },
                    onDelete = {
                        journalViewModel.deleteJournal(journal.id)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
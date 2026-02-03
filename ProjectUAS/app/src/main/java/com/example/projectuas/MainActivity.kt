package com.example.projectuas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.projectuas.data.GoogleAuthUIClient
import com.example.projectuas.navigation.AppNavigation
import com.example.projectuas.ui.theme.ProjectUASTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainActivity : ComponentActivity() {

    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Memastikan data selalu sinkron dengan server
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        setContent {
            ProjectUASTheme {
                AppNavigation(googleAuthUIClient)
            }
        }
    }
}
package com.onyenze.messageencryptor.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.onyenze.messageencryptor.MainActivity
import com.onyenze.messageencryptor.utils.PlainTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordReset(
    navController: NavHostController,
    authState: FirebaseAuth,
    activity: MainActivity
) {
    Scaffold(
        topBar = { PlainTopBar() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            
        }
    }
}
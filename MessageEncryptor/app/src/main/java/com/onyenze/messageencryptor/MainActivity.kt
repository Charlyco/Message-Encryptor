package com.onyenze.messageencryptor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.onyenze.messageencryptor.screens.AuthScreen
import com.onyenze.messageencryptor.screens.Home
import com.onyenze.messageencryptor.screens.PasswordReset
import com.onyenze.messageencryptor.screens.SavedKeys
import com.onyenze.messageencryptor.screens.SignUpScreen
import com.onyenze.messageencryptor.screens.WelcomeScreen
import com.onyenze.messageencryptor.ui.theme.MessageEncryptorTheme
import com.onyenze.messageencryptor.utils.DataStoreManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var authState: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager.getInstance(applicationContext)
        authState = Firebase.auth
        setContent {
            MessageEncryptorTheme {
                CodexApp(dataStoreManager, authState, this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUser = authState.currentUser
        if (currentUser != null) {
            lifecycleScope.launch { dataStoreManager.writeAuthData(currentUser.email.toString()) }
        }
    }
}
@Composable
fun CodexApp(dataStoreManager: DataStoreManager, authState: FirebaseAuth, activity: MainActivity) {
    val navController = rememberNavController()
    val owner = LocalViewModelStoreOwner.current
    val mainViewModel: MainViewModel? =
        owner?.let {
        viewModel(it, "MainViewModel", MainViewModelFactory()
        )
    }
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController, dataStoreManager)
        }
        composable("home") {
            Home(navController = navController, mainViewModel, dataStoreManager, activity)
        }
        composable("auth") {
            AuthScreen(navController = navController, authState, activity)
        }
        composable("sign_up") {
            SignUpScreen(navController = navController, authState, activity, dataStoreManager)
        }
        composable("password") {
            PasswordReset(navController = navController, authState, activity)
        }
        composable("saved_keys") {
            SavedKeys(navController = navController, dataStoreManager = dataStoreManager, mainViewModel)
        }
    }
}






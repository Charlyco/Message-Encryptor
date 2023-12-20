package com.onyenze.messageencryptor.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.onyenze.messageencryptor.MainActivity
import com.onyenze.messageencryptor.R
import com.onyenze.messageencryptor.entity.User
import com.onyenze.messageencryptor.utils.DataStoreManager
import com.onyenze.messageencryptor.utils.PlainTopBar
import com.onyenze.messageencryptor.utils.ProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    authState: FirebaseAuth,
    activity: MainActivity,
    dataStoreManager: DataStoreManager,
) {
    Scaffold(
        topBar = { PlainTopBar() }
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp - 64
        Surface(
            Modifier.padding(it)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.create_account_header),
                    fontSize = TextUnit(22.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(16.dp))
                InputBoxesCard(
                    Modifier,
                    navController,
                    screenWidth,
                    authState,
                    activity,
                    dataStoreManager
                )
                Spacer(modifier = Modifier.height(64.dp))
                SignInInstead(navController)
            }
        }
    }
}

@Composable
fun SignInInstead(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = stringResource(id = R.string.have_account),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(id = R.string.sign_in),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate("auth") {
                    launchSingleTop = true
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBoxesCard(
    modifier: Modifier,
    navController: NavController,
    screenWidth: Int,
    authState: FirebaseAuth,
    activity: MainActivity,
    dataStoreManager: DataStoreManager,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(value = false) }
    val user = User("", "", "")
    val context = LocalContext.current.applicationContext
    var showIndicator by remember{ mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    user.email = email
                },
                placeholder = { Text(text = stringResource(id = R.string.email)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onTertiary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "") },
                modifier = modifier
                    .paddingFromBaseline(top = 10.dp)
                    .width(screenWidth.dp)
                    .height(48.dp)
            )
        }
        Spacer(modifier = modifier.height(16.dp))
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    user.password = password
                },
                placeholder = { Text(text = stringResource(id = R.string.password)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onTertiary
                ),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "") },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    if (showPassword) {
                        IconButton(onClick = { showPassword = false }) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "hide_password"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { showPassword = true }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "hide_password"
                            )
                        }
                    }
                },
                modifier = modifier
                    .paddingFromBaseline(top = 10.dp)
                    .height(48.dp)
                    .width(screenWidth.dp)
            )
        }
        Spacer(modifier = modifier.height(24.dp))
        ProgressIndicator(showIndicator)
        Button(
            modifier = modifier
                .width(screenWidth.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            onClick = {
                signUp(
                    email,
                    password,
                    navController,
                    authState,
                    context,
                    activity,
                    dataStoreManager,
                    coroutineScope
                    )
                showIndicator = true
                      },
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(id = R.string.create_account),
                fontSize = TextUnit(14.0F, TextUnitType.Sp),
            )
        }
    }
}

fun signUp(
    email: String,
    password: String,
    navController: NavController,
    authState: FirebaseAuth,
    context: Context,
    activity: MainActivity,
    dataStoreManager: DataStoreManager,
    coroutineScope: CoroutineScope
) {
    authState.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val currentUser = task.result.user
                currentUser?.sendEmailVerification()
                Toast.makeText(context,
                    "verification email sent to " + currentUser?.email,
                    Toast.LENGTH_LONG).show()
                navController.navigate("auth") {
                    launchSingleTop = true
                }
                coroutineScope.launch { dataStoreManager.writeAuthData(currentUser?.email.toString()) }
            } else {
                // If sign in fails, display a message to the user.
                Log.w("SignUp", "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    context,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
}
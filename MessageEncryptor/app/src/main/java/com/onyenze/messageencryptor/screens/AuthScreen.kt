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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.onyenze.messageencryptor.MainActivity
import com.onyenze.messageencryptor.R
import com.onyenze.messageencryptor.entity.User
import com.onyenze.messageencryptor.utils.PlainTopBar
import com.onyenze.messageencryptor.utils.ProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavHostController, authState: FirebaseAuth, activity: MainActivity) {
        val screenWidth = LocalConfiguration.current.screenWidthDp - 64
    Scaffold(
        topBar = {PlainTopBar()}
    ) {
        Surface(
            Modifier
                .padding(it)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier
                    .padding(top = 32.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_back),
                    fontSize = TextUnit(22.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.sign_in_header),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),

                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthInputBoxesCard(
                    Modifier,
                    navController,
                    screenWidth,
                    authState,
                    activity
                )
                Spacer(modifier = Modifier.height(64.dp))
                SignUpInstead(navController)
            }
        }
    }
}

@Composable
fun SignUpInstead(navController: NavHostController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = stringResource(id = R.string.no_account),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(id = R.string.create_account),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate("sign_up") {
                    launchSingleTop = true
                    popUpTo("auth") { inclusive = true }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthInputBoxesCard(
    modifier: Modifier,
    navController: NavController,
    screenWidth: Int,
    authState: FirebaseAuth,
    activity: MainActivity )
    {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var showPassword by remember { mutableStateOf(value = false) }
        val user = User("", "", "")
        val context = LocalContext.current.applicationContext
        var showIndicator by remember{ mutableStateOf(false) }

        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 72.dp)
        ) {
            val (emailBox, passwordBox, indicator, forgotPassword, button) = createRefs()

            Surface(
                modifier = modifier
                    .constrainAs(emailBox) {
                    top.linkTo(parent.top, margin = 4.dp)
                    centerHorizontallyTo(parent)
                },
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
            Surface(
                modifier = modifier
                    .constrainAs(passwordBox) {
                        top.linkTo(emailBox.bottom, margin = 24.dp)
                        centerHorizontallyTo(parent)
                    },
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
                leadingIcon = {Icon(imageVector = Icons.Default.Lock, contentDescription = "")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                }else {
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
            Text(
                text = stringResource(id = R.string.forgot_password),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .constrainAs(forgotPassword) {
                        top.linkTo(passwordBox.bottom, margin = 2.dp)
                        end.linkTo(passwordBox.end)
                    }
                    .clickable {
                        navController.navigate("password") {
                            launchSingleTop = true
                            popUpTo("auth") { inclusive = true }
                        }
                    }
            )
            Surface(
                modifier.constrainAs(indicator) {
                    top.linkTo(passwordBox.bottom)
                    centerHorizontallyTo(parent)
                },
                color = MaterialTheme.colorScheme.onTertiary
            ) {
                ProgressIndicator(showIndicator)
            }
            Button(
                modifier = modifier
                    .width(screenWidth.dp)
                    .constrainAs(button) {
                        top.linkTo(indicator.bottom, margin = 32.dp)
                        centerHorizontallyTo(parent)
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                onClick = {
                    if (email.length > 2 && password.length > 2) {
                        signIn(
                            email,
                            password,
                            navController,
                            authState,
                            context,
                            activity)
                        showIndicator = true
                    }else {
                        Toast.makeText(context, "Email or password invalid or empty", Toast.LENGTH_LONG).show()
                    }
                          },
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in),
                    fontSize = TextUnit(14.0F, TextUnitType.Sp),
                )
            }
        }
    }

fun signIn(
    email: String,
    password: String,
    navController: NavController,
    authState: FirebaseAuth,
    context: Context?,
    activity: MainActivity
) {
    authState.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val currentUser = task.result.user
                if (currentUser?.isEmailVerified == true) {
                    navController.navigate("home")
                }else {
                    Toast.makeText(
                        context,
                        "Open your mailbox and click verification email, then try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Log.w("SignIn", "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    context,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
}

@Preview
@Composable
fun PreviewAuthScreen() {
    AuthScreen(navController = rememberNavController(),
        authState = FirebaseAuth.getInstance(),
        activity = MainActivity()
    )
}
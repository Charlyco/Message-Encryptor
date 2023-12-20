package com.onyenze.messageencryptor.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.onyenze.messageencryptor.R
import com.onyenze.messageencryptor.utils.DataStoreManager
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavController, dataStoreManager: DataStoreManager) {
    ConstraintLayout(Modifier.fillMaxSize()) {
        val (logo, text) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "app icon",
            modifier = Modifier
                .size(72.dp)
                .constrainAs(logo) {
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
            },
            contentScale = ContentScale.Fit
        )
    }
    LaunchedEffect(key1 = true) {
        delay(4000)
        if (dataStoreManager.readAuthData() != null) {
            navController.navigate("auth") {
                popUpTo("welcome") {inclusive = true}
            }
        }else {
            navController.navigate("sign_up") {
                popUpTo("welcome") {inclusive = true}
            }
        }
    }
}
package com.onyenze.messageencryptor.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.onyenze.messageencryptor.R

@Composable
fun ProgressIndicator(showIndicator: Boolean) {
    if (showIndicator) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTopBar() {
    TopAppBar(
        title = {  },
        navigationIcon = {
            Image(painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "",
                Modifier.size(48.dp),
                contentScale = ContentScale.Fit
                )
            },
        colors = TopAppBarDefaults.smallTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.background
        )
        )
}
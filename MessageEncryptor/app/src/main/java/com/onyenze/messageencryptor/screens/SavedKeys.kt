package com.onyenze.messageencryptor.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.onyenze.messageencryptor.MainViewModel
import com.onyenze.messageencryptor.utils.DataStoreManager
import com.onyenze.messageencryptor.utils.PlainTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedKeys(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    mainViewModel: MainViewModel?
) {
    val listOfKeys = mainViewModel?.savedKeys?.observeAsState()?.value
    Scaffold(
        topBar = { PlainTopBar()}
    ) {
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
            ) {
            LazyColumn(contentPadding = PaddingValues(top = 4.dp)) {
                if (listOfKeys != null) {
                    items(listOfKeys) {item ->
                        KeyItem(item, navController, mainViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun KeyItem(item: String, navController: NavController, mainViewModel: MainViewModel) {
    Surface(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 4.dp
    ) {
        Text(
            text = item,
            Modifier.clickable {
                mainViewModel.encryptionKey.value = item
                navController.navigate("home") {
                    launchSingleTop = true
                }
            }
        )
    }
}

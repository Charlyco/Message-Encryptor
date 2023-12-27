package com.onyenze.messageencryptor.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.onyenze.messageencryptor.MainViewModel
import com.onyenze.messageencryptor.R
import com.onyenze.messageencryptor.utils.DataStoreManager
import com.onyenze.messageencryptor.utils.LevelCheckBox
import com.onyenze.messageencryptor.utils.PlainTopBar
import com.onyenze.messageencryptor.utils.TopBarWithNavButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(dataStoreManager: DataStoreManager, navController: NavController, mainViewModel: MainViewModel?) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopBarWithNavButton(route = "home", navController = navController) }
    ) {
       Surface(
           Modifier.padding(it),
           color = MaterialTheme.colorScheme.background
       ) {
       LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
               item{
                   Text(
                       text = stringResource(id = R.string.level_setting),
                   )
                   Surface(
                       modifier = Modifier
                           .fillMaxWidth(),
                       color = MaterialTheme.colorScheme.background,
                       contentColor = MaterialTheme.colorScheme.onBackground,
                       border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                   ) {
                       LevelCheckBox(dataStoreManager, coroutineScope)
                   }
               }
           }
       }
    }
}
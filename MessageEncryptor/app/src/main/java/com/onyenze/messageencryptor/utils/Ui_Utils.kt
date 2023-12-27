package com.onyenze.messageencryptor.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.onyenze.messageencryptor.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithNavButton(route: String, navController: NavController) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(
                onClick = { navController.navigate(route) { launchSingleTop = true } },
                ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "arrow back")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

var selectedLevel by mutableStateOf(Levels.Standard)
@Composable
fun LevelCheckBox(dataStoreManager: DataStoreManager, coroutineScope: CoroutineScope
) {
    var selectedOption by remember { mutableStateOf(Levels.Standard.name) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Levels.entries.forEach { level ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .selectable(
                        selected = level.name == selectedOption,
                        onClick = {
                            selectedOption = level.name
                            coroutineScope.launch { dataStoreManager.writeLevelData(level) }
                        }
                    )
            ) {
                RadioButton(
                    selected = level.name == selectedOption,
                    onClick  = {
                        selectedOption = level.name
                        coroutineScope.launch { dataStoreManager.writeLevelData(level) }
                    })
                Text(text = level.name)
                selectedLevel = level
            }
        }
    }
}
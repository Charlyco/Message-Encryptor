package com.onyenze.messageencryptor.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.onyenze.messageencryptor.MainViewModel
import com.onyenze.messageencryptor.utils.DataStoreManager
import com.onyenze.messageencryptor.utils.PlainTopBar
import com.onyenze.messageencryptor.utils.TopBarWithNavButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedKeys(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    mainViewModel: MainViewModel?
) {
    val listOfKeys = mainViewModel?.savedKeys?.observeAsState()?.value
    Scaffold(
        topBar = { TopBarWithNavButton(route = "home", navController = navController)}
    ) {
        ScreenContents(
            modifier = Modifier.padding(it),
            navController,
            mainViewModel,
            dataStoreManager,
            listOfKeys
            )
    }
}

@Composable
fun ScreenContents(
    modifier: Modifier,
    navController: NavController,
    mainViewModel: MainViewModel?,
    dataStoreManager: DataStoreManager,
    listOfKeys: MutableList<String>?
) {
    Surface(
        modifier = Modifier
            .padding(top = 64.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(contentPadding = PaddingValues(top = 4.dp)) {
            if (listOfKeys != null) {
                items(listOfKeys) {item ->
                    KeyItem(item, listOfKeys.indexOf(item), navController, mainViewModel, dataStoreManager)
                }
            }
        }
    }
}

@Composable
fun KeyItem(
    item: String,
    indexOfItem: Int,
    navController: NavController,
    mainViewModel: MainViewModel?,
    dataStoreManager: DataStoreManager
) {
    val coroutineScope = rememberCoroutineScope()
    var itemRemoved: Boolean? = false
    val context = LocalContext.current
    Surface(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 4.dp
    ) {
        ConstraintLayout(
            ) {
            val (text, button) = createRefs()
            Text(
                text = item,
                Modifier
                    .constrainAs(text) {
                        start.linkTo(parent.start, margin = 4.dp)
                    }
                    .clickable {
                    mainViewModel?.encryptionKey?.value = item
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                }
            )
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        itemRemoved = mainViewModel?.deleteKey(indexOfItem, dataStoreManager)
                        if (itemRemoved == true) {
                            Toast.makeText(context, "The key" + item + "deleted successfully", Toast.LENGTH_LONG).show()
                        }
                    }
                          },
                Modifier.constrainAs(button) {
                    end.linkTo(parent.end, margin = 2.dp)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "")
            }
        }
    }
}

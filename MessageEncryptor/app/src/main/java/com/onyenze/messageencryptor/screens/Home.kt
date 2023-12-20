package com.onyenze.messageencryptor.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material.icons.filled.NoEncryption
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.onyenze.messageencryptor.MainActivity
import com.onyenze.messageencryptor.MainViewModel
import com.onyenze.messageencryptor.R
import com.onyenze.messageencryptor.utils.DataStoreManager
import com.onyenze.messageencryptor.utils.Levels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    mainViewModel: MainViewModel?,
    dataStoreManager: DataStoreManager,
    activity: MainActivity
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    Scaffold(
        topBar = {HomeToolBar(navController, mainViewModel, dataStoreManager)},
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        HomeScreen(Modifier.padding(it), navController, screenWidth, mainViewModel, dataStoreManager, activity)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeToolBar(
    navController: NavController,
    mainViewModel: MainViewModel?,
    dataStoreManager: DataStoreManager
) {
    var onShowMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name))},
        navigationIcon = { Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "",
            Modifier.size(48.dp),
            contentScale = ContentScale.Fit
            )
                         },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.background
        ),
        actions = {
            IconButton(onClick = { onShowMenu = !onShowMenu }) {
                Icon(Icons.Default.DensityMedium, contentDescription = "Options menu ")
            }
            DropdownMenu(expanded = onShowMenu, onDismissRequest = { onShowMenu = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.view_keys)) },
                    onClick = {
                        coroutineScope.launch {
                            mainViewModel?.savedKeys?.value = dataStoreManager.readEncryptData()?.keyList
                        }
                        navController.navigate("saved_keys") {
                            launchSingleTop = true
                        }
                    })
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.settings)) },
                    onClick = { /*TODO*/ })
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.sign_out)) },
                    onClick = {
                        navController.navigate("auth") {
                            popUpTo("home") {inclusive = true}
                        }
                    })
            }
        }
        )
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    screenWidth: Int,
    mainViewModel: MainViewModel?,
    dataStoreManager: DataStoreManager,
    activity: MainActivity
) {
    val coroutineScope = rememberCoroutineScope()
    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
            item { SecretKeys(screenWidth, coroutineScope, mainViewModel, dataStoreManager) }
            item { MessageInput(screenWidth, coroutineScope, mainViewModel) }
            item { OutPut(screenWidth, coroutineScope, mainViewModel, activity) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(screenWidth: Int, coroutineScope: CoroutineScope, mainViewModel: MainViewModel?) {
    val modifier = Modifier
    val context = LocalContext.current.applicationContext
    val inputLivedata = mainViewModel?.message?.observeAsState()?.value!!
    var input by rememberSaveable { mutableStateOf("") }

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (inputBox, decode, paste) = createRefs()

        Surface(modifier
            .constrainAs(inputBox) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top, margin = 16.dp) },
            shape = MaterialTheme.shapes.small,
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
            ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier
                    .height(184.dp)
                    .width(screenWidth.dp),
                placeholder = { Text(text = stringResource(id = R.string.input_message)) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
        Button(
            onClick = { coroutineScope.launch { mainViewModel.transpose(input) } },
            modifier
                .height(48.dp)
                .constrainAs(decode) {
                    end.linkTo(inputBox.end, margin = 0.dp)
                    top.linkTo(inputBox.bottom, margin = 8.dp)
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ),
            shape = MaterialTheme.shapes.medium
            ) {
            Row(
                modifier = modifier.padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(modifier = modifier.padding(start = 4.dp),
                    text = stringResource(id = R.string.decode))
                IconButton(onClick = {coroutineScope.launch { mainViewModel.transpose(input) }}) {
                    Icon(imageVector = Icons.Filled.NoEncryption, contentDescription = "share output")
                }
            }
        }
        Surface(
            modifier
                .clickable {
                    coroutineScope.launch {
                        mainViewModel.pasteText(context)
                        input = inputLivedata
                    }
                }
                .constrainAs(paste) {
                    end.linkTo(decode.start, margin = 16.dp)
                    top.linkTo(inputBox.bottom, margin = 8.dp)
                },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            color = MaterialTheme.colorScheme.background
        ) {
            IconButton(
                onClick = {coroutineScope.launch {
                    mainViewModel.pasteText(context)
                    input = inputLivedata
                }},
            ) {
                Icon(imageVector = Icons.Filled.ContentPaste, contentDescription = "share output")
            }
        }
    }
}


@Composable
fun SecretKeys(
    screenWidth: Int,
    coroutineScope: CoroutineScope,
    mainViewModel: MainViewModel?,
    dataStoreManager: DataStoreManager
) {
    val modifier = Modifier
    ConstraintLayout(
        modifier
            .padding(top = 64.dp)
            .fillMaxWidth()
    ) {
        val (key, copyKey, pasteKey, generateKey, save, level) = createRefs()
        val keyLiveData = mainViewModel?.encryptionKey?.observeAsState()?.value
        val context = LocalContext.current.applicationContext

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .constrainAs(level) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            LevelCheckBox()
        }
        Surface(
            modifier
                .width(screenWidth.dp)
                .height(48.dp)
                .constrainAs(key) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(level.bottom, margin = 8.dp)
                },
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            color = MaterialTheme.colorScheme.background
        ) {
            if (keyLiveData != null) {
                Text(
                    modifier = modifier.padding(horizontal = 4.dp),
                    text = keyLiveData,
                )
            }
        }
        Surface(
            modifier
                .size(48.dp)
                .clickable { coroutineScope.launch { mainViewModel?.copyKey(context) } }
                .constrainAs(copyKey) {
                    start.linkTo(key.start, margin = 0.dp)
                    top.linkTo(key.bottom, margin = 8.dp)
                },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            color = MaterialTheme.colorScheme.background
        ) {
            IconButton(
                onClick = { coroutineScope.launch { mainViewModel?.copyKey(context) } },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "copy contents")
            }
        }
        Surface(
            modifier
                .size(48.dp)
                .clickable {
                    coroutineScope.launch {
                        mainViewModel?.pasteKey(context)
                    }
                }
                .constrainAs(pasteKey) {
                    start.linkTo(copyKey.end, margin = 8.dp)
                    top.linkTo(key.bottom, margin = 8.dp)
                },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            color = MaterialTheme.colorScheme.background
        ) {
            IconButton(
                onClick = {coroutineScope.launch {
                    mainViewModel?.pasteKey(context)
                }},

                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(imageVector = Icons.Filled.ContentPaste, contentDescription = "paste key")
            }
        }
        Surface(
            modifier
                .size(48.dp)
                .clickable {
                    coroutineScope.launch {
                        mainViewModel?.updateKeyList(dataStoreManager)
                    }
                }
                .constrainAs(save) {
                    top.linkTo(key.bottom, margin = 8.dp)
                    start.linkTo(pasteKey.end, margin = 8.dp)
                },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            color = MaterialTheme.colorScheme.background
        ) {
            IconButton(
                onClick = { coroutineScope.launch {
                    mainViewModel?.updateKeyList(dataStoreManager)
                }},
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(imageVector = Icons.Filled.Save, contentDescription = "generate key")
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    mainViewModel?.generateKey(selectedLevel)
                }
            },
            modifier
                .height(48.dp)
                .constrainAs(generateKey) {
                    top.linkTo(key.bottom, margin = 8.dp)
                    end.linkTo(key.end, margin = 0.dp)
                },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.background
            ),
            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.generate))
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            mainViewModel?.generateKey(selectedLevel)
                        }
                    },
                ) {
                    Icon(imageVector = Icons.Filled.GeneratingTokens, contentDescription = "generate key")
                }
            }
        }
    }
}

@Composable
fun OutPut(
    screenWidth: Int,
    coroutineScope: CoroutineScope,
    mainViewModel: MainViewModel?,
    activity: MainActivity
) {
    val modifier = Modifier
    val outputLiveData = mainViewModel?.output?.observeAsState()?.value!!
    val context = LocalContext.current.applicationContext
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        val (header, outputBox, copy, share) = createRefs()

        Text(
            text = stringResource(id = R.string.output_header),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            modifier = modifier
                .constrainAs(header) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                }
        )
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            modifier = modifier
                .height(184.dp)
                .width(screenWidth.dp)
                .constrainAs(outputBox) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(header.bottom, margin = 4.dp)
                }
        ) {
            Text(
                modifier = modifier.padding(horizontal = 4.dp),
                text = outputLiveData
            )
        }
        Surface(
            modifier
                .size(48.dp)
                .clickable {
                    coroutineScope.launch {
                        mainViewModel.copyOutput(context, outputLiveData)
                    }
                }
                .constrainAs(copy) {
                    end.linkTo(share.start, margin = 16.dp)
                    top.linkTo(outputBox.bottom, margin = 8.dp)
                },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            color = MaterialTheme.colorScheme.background
        ) {
            IconButton(
                onClick = {coroutineScope.launch {
                    mainViewModel.copyOutput(context, outputLiveData)
                }},

                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            ) {
                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "copy output")
            }
        }

        Button(
            onClick = { openSendingApp(outputLiveData, activity) },
            modifier
                .height(48.dp)
                .constrainAs(share) {
                    end.linkTo(outputBox.end)
                    top.linkTo(outputBox.bottom, margin = 8.dp)
                },
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
            ) {
            Row(modifier = modifier.padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.share),
                )
                IconButton(
                    onClick = { openSendingApp(outputLiveData, activity) },
                ) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "share output")
                }
            }
        }
    }
}


fun openSendingApp(outputLiveData: String, activity: MainActivity) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, "Confidential Message")
    intent.putExtra(Intent.EXTRA_TEXT, outputLiveData)
    val chooser = Intent.createChooser(intent, "Select an app to send message" )
    try {
        activity.startActivity(chooser)
    } catch (e: ActivityNotFoundException) {
        Log.i("SHARE", e.message.toString())
    }
}

var selectedLevel by mutableStateOf(Levels.Standard)
@Composable
fun LevelCheckBox(
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
                        onClick = { selectedOption = level.name }
                    )
            ) {
                RadioButton(
                    selected = level.name == selectedOption,
                    onClick  = {
                        selectedOption = level.name
                    })
                Text(text = level.name)
                selectedLevel = level
            }
        }
    }
}
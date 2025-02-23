package com.example.mariotodolist.trackList

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mariotodolist.R
import com.example.mariotodolist.entities.CheckListEntity
import kotlinx.coroutines.launch

@Composable
fun TrackScreen() {
    val vm: TrackViewModel = viewModel()
    val uiState = vm.uiState.collectAsState().value

    TrackScreen(
        isLoading = uiState.loading,
        isModalExpaned = uiState.isModalExpanded,
        taskList = uiState.taskList,
        onItemClicked = vm::onTaskCompleted,
        onAddTask = vm::onAddTask,
        onClearTask = vm::onClearTask,
        shouldScroll = uiState.shouldScroll,
        onScrolled = vm::onScrolled,
        onDismissModal = vm::onDismissModal,
        onTaskAdded = vm::onTaskAdded,
        currentValue = uiState.textFieldValue,
        onTextFieldChange = vm::onTextChanged
    )

    PlaySound(shouldPlay = uiState.playSound, afterPlayActon = vm::stopSound)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackScreen(
    isLoading: Boolean,
    isModalExpaned: Boolean,
    taskList: List<CheckListEntity>,
    onItemClicked: (Int) -> Unit,
    onAddTask: () -> Unit,
    onClearTask: () -> Unit,
    shouldScroll: Boolean,
    onScrolled: () -> Unit,
    onDismissModal: () -> Unit,
    onTaskAdded: (String) -> Unit,
    currentValue: String,
    onTextFieldChange: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        //.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("List") },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        onAddTask()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = ""
                        )
                    }

                    IconButton(onClick = {
                        onClearTask()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AddComponentBottomSheet(
            isModalExpanded = isModalExpaned,
            onDismissModal = onDismissModal,
            onTaskAdded = onTaskAdded,
            currentValue = currentValue,
            onTextFieldChange = onTextFieldChange
        )

        TaskList(scrollState, innerPadding, isLoading, taskList, onItemClicked)

    }

    LaunchedEffect(shouldScroll) {
        if (shouldScroll && taskList.isNotEmpty()) {
            coroutineScope.launch {
                scrollState.animateScrollToItem(taskList.size - 1)
                onScrolled()
            }
        }
    }
}

@Composable
private fun TaskList(
    scrollState: LazyListState,
    innerPadding: PaddingValues,
    isLoading: Boolean,
    taskList: List<CheckListEntity>,
    onItemClicked: (Int) -> Unit
) {
    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.primaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            item { CircularProgressIndicator() }
        } else {
            itemsIndexed(taskList) { index, item ->
                Row(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(0.8f),
                        text = item.title
                    )
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = {
                            onItemClicked(item.id)
                        },
                    )
                }
                Spacer(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .fillParentMaxWidth(0.95f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun PlaySound(
    shouldPlay: Boolean,
    afterPlayActon: () -> Unit
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    if (shouldPlay) {
        LaunchedEffect(Unit) {
            mediaPlayer = MediaPlayer.create(context, R.raw.coin)
            mediaPlayer?.setOnCompletionListener {
                it.release()
                mediaPlayer = null
                afterPlayActon()
            }
            mediaPlayer?.start()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddComponentBottomSheet(
    isModalExpanded: Boolean,
    onDismissModal: () -> Unit,
    onTaskAdded: (String) -> Unit,
    currentValue: String,
    onTextFieldChange: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    if (isModalExpanded) {
        ModalBottomSheet(
            modifier = Modifier.wrapContentSize(),
            sheetState = sheetState,
            onDismissRequest = {
                onTextFieldChange("")
                onDismissModal()
            },
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = currentValue,
                    onValueChange = {
                        onTextFieldChange(it)
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { onTaskAdded(currentValue) }
                ) {
                    Text("Add Task")
                }
            }
        }
    }
}

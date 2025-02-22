package com.example.mariotodolist.trackList

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
        taskList = uiState.taskList,
        onItemClicked = vm::onTaskCompleted,
        onAddTask = vm::onAddTask,
        shouldScroll = uiState.shouldScroll,
        onScrolled = vm::onScrolled
    )
    PlaySound(shouldPlay = uiState.playSound, afterPlayActon = vm::stopSound)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackScreen(
    isLoading: Boolean,
    taskList: List<CheckListEntity>,
    onItemClicked: (Int) -> Unit,
    onAddTask: () -> Unit,
    shouldScroll: Boolean,
    onScrolled: () -> Unit
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
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primaryContainer)
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
                        Text("Item ${index + 1}")
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = {
                                onItemClicked(item.id)
                            },
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(shouldScroll) {
        if(shouldScroll){
            coroutineScope.launch {
                scrollState.animateScrollToItem(taskList.size - 1)
                onScrolled()
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

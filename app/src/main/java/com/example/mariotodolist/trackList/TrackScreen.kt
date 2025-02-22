package com.example.mariotodolist.trackList

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mariotodolist.R
import com.example.mariotodolist.entities.CheckListEntity

@Composable
fun TrackScreen() {
    val vm: TrackViewModel = viewModel()
    val uiState = vm.uiState.collectAsState().value
    TrackScreen(
        isLoading = uiState.loading,
        taskList = uiState.taskList,
        onItemClicked = vm::onTaskCompleted
    )
    PlaySound(shouldPlay = uiState.playSound, afterPlayActon = vm::stopSound)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackScreen(
    isLoading: Boolean,
    taskList: List<CheckListEntity>,
    onItemClicked: (Int) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("List") },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
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
            mediaPlayer = MediaPlayer.create(context, R.raw.mario_coin_sound)
            mediaPlayer?.setOnCompletionListener {
                it.release()
                mediaPlayer = null
                afterPlayActon()
            }
            mediaPlayer?.start()

        }
    }
}

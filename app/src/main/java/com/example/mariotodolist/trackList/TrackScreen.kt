package com.example.mariotodolist.trackList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TrackScreen() {
    val vm: TrackViewModel = viewModel()
    val uiState = vm.uiState.collectAsState()
    TrackScreen(
        uiState = uiState.value
    )
}

@Composable
private fun TrackScreen(
    uiState: TrackState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            if (uiState.loading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ) {
                    val list = List(50) { it + 1 }
                    itemsIndexed(list) { index, item ->
                        Row(
                            modifier = Modifier.fillParentMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Item ${index + 1}")
                            Checkbox(
                                checked = false,
                                onCheckedChange = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
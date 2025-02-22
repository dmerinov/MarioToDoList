package com.example.mariotodolist.trackList

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(
        TrackState(
            loading = false
        )
    )
    val uiState = _uiState

    init {
        startConfigurations()
    }

    private fun startConfigurations() {
        _uiState.value.copy(
            loading = true
        )
    }
}
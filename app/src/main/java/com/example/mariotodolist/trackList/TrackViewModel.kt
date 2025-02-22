package com.example.mariotodolist.trackList

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
        showLoading()
        //perform some shit
        hideLoading()
    }

    private fun hideLoading() {
        _uiState.update { state ->
            state.copy(
                loading = false
            )
        }
    }

    private fun showLoading() {
        _uiState.update { state ->
            state.copy(
                loading = true
            )
        }
    }
}
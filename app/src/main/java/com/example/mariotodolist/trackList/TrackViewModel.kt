package com.example.mariotodolist.trackList

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mariotodolist.entities.CheckListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(
        TrackState(
            loading = false,
            taskList = listOf(),
            playSound = false,
            shouldScroll = false,
            isModalExpanded = false,
            textFieldValue = ""
        )
    )
    val uiState = _uiState
    private val _taskList = mutableStateListOf<CheckListEntity>()

    init {
        startConfigurations()
    }

    private fun startConfigurations() {
        showLoading()
        //perform some shit
        _uiState.update {
            it.copy(
                taskList = _taskList
            )
        }
        hideLoading()
    }

    fun onTaskCompleted(index: Int) {
        checkItem(index)
    }

    private fun checkItem(index: Int) {
        val currentValue = _taskList[index].isChecked
        _taskList[index] = _taskList[index].copy(isChecked = !currentValue)
        _uiState.update { state ->
            state.copy(
                taskList = _taskList,
                playSound = !currentValue
            )
        }
    }

    fun onTextChanged(text: String) {
        _uiState.update { state ->
            state.copy(
                textFieldValue = text
            )
        }
    }

    fun onTaskAdded(task: String) {
        onDismissModal()
        _taskList.add(
            CheckListEntity(
                id = _taskList.size,
                title = task,
                isChecked = false
            )
        )
        _uiState.update {
            it.copy(
                shouldScroll = true,
                taskList = _taskList,
                textFieldValue = ""
            )
        }
    }

    fun onDismissModal() {
        _uiState.update { state ->
            state.copy(
                isModalExpanded = false
            )
        }
    }

    fun onAddTask() {
        _uiState.update { state ->
            state.copy(
                isModalExpanded = true
            )
        }
    }

    fun onClearTask(){
        _taskList.clear()
        _uiState.update { state ->
            state.copy(
                taskList = _taskList
            )
        }
    }

    fun onScrolled() {
        _uiState.update {
            it.copy(shouldScroll = false)
        }
    }

    fun stopSound() {
        _uiState.update { state ->
            state.copy(
                playSound = false
            )
        }
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
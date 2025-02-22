package com.example.mariotodolist.trackList

import com.example.mariotodolist.entities.CheckListEntity

data class TrackState(
    val loading: Boolean,
    val taskList: List<CheckListEntity>,
    val playSound: Boolean,
    val shouldScroll: Boolean
)
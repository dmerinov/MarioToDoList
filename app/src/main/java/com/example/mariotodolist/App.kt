package com.example.mariotodolist

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mariotodolist.trackList.TrackScreen
import com.example.mariotodolist.ui.theme.MarioToDoListTheme
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application()

@Composable
fun MyAppContent() {
    MarioToDoListTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable(route = "main") {
                TrackScreen()
            }
        }
    }
}
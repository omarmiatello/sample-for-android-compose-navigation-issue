package com.github.omarmiatello.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var bool by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                while (true) { // "animation" loop
                    delay(1)
                    bool = !bool
                }
            }
            remember { println("I will be always remembered!") }
            Text(text = "Current value: $bool")
            NavHost(
                navController = rememberNavController(),
                startDestination = "test1",
            ) {
                composable(route = "test1") {
                    remember { println("I will be remembered, but sometimes forgotten (race condition)") }
                    DisposableEffect(Unit) {
                        onDispose { println("disposed (current lifecycle: ${it.lifecycle.currentState}) - triggered by onForgotten()") }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        println("onStop()")
    }
}
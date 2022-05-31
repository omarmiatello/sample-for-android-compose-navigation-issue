package com.github.omarmiatello.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.omarmiatello.composetest.databinding.FragmentExampleBinding
import kotlinx.coroutines.delay

class MainActivity : FragmentActivity() {
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
            NavHost(
                navController = rememberNavController(),
                startDestination = "test1",
            ) {
                composable(route = "test1") {
                    AndroidViewBinding({ inflater, parent, attachToParent ->
                        FragmentExampleBinding.inflate(inflater, parent, attachToParent)
                    })
                }
            }
            Text(text = "Current value: $bool")
        }
    }
}
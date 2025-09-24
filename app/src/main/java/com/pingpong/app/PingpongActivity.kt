package com.pingpong.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.pingpong.app.ui.PingpongApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PingpongActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PingpongApp()
        }
    }
}

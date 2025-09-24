package com.pingpong.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.pingpong.app.ui.navigation.PingpongNavHost
import com.pingpong.app.ui.theme.PingpongTheme

@Composable
fun PingpongApp() {
    PingpongTheme {
        val navController = rememberNavController()
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            PingpongNavHost(navController = navController)
        }
    }
}

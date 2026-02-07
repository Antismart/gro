package com.example.gro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gro.ui.navigation.GroNavGraph
import com.example.gro.ui.theme.GroTheme
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityResultSender = ActivityResultSender(this)
        enableEdgeToEdge()
        setContent {
            GroTheme {
                GroNavGraph(activityResultSender = activityResultSender)
            }
        }
    }
}

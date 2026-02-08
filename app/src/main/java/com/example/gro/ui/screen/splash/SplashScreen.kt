package com.example.gro.ui.screen.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.LoraFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToGarden: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val destination by viewModel.destination.collectAsState()
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "splashAlpha",
    )

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    LaunchedEffect(destination) {
        if (destination != null) {
            delay(1500)
            when (destination) {
                SplashDestination.Garden -> onNavigateToGarden()
                SplashDestination.Onboarding -> onNavigateToOnboarding()
                null -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Gr\u014D",
            fontFamily = LoraFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 56.sp,
            color = GroGreen,
            modifier = Modifier.alpha(alpha),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSplash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Gr\u014D",
            fontFamily = LoraFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 56.sp,
            color = GroGreen,
        )
    }
}

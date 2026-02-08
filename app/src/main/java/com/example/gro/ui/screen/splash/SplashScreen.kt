package com.example.gro.ui.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSage
import com.example.gro.ui.theme.LoraFamily
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

    val vineProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
        vineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        )
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawVines(vineProgress.value)
        }

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

private fun DrawScope.drawVines(progress: Float) {
    if (progress <= 0f) return

    val cx = size.width / 2f
    val cy = size.height / 2f

    val vineConfigs = listOf(
        VineConfig(startAngle = -90f, curveDir = 1f, maxLength = size.height * 0.35f),
        VineConfig(startAngle = 90f, curveDir = -1f, maxLength = size.height * 0.35f),
        VineConfig(startAngle = -45f, curveDir = -1f, maxLength = size.width * 0.3f),
        VineConfig(startAngle = 225f, curveDir = 1f, maxLength = size.width * 0.3f),
    )

    vineConfigs.forEach { config ->
        drawVine(cx, cy, config, progress)
    }
}

private data class VineConfig(
    val startAngle: Float,
    val curveDir: Float,
    val maxLength: Float,
)

private fun DrawScope.drawVine(
    cx: Float,
    cy: Float,
    config: VineConfig,
    progress: Float,
) {
    val steps = 20
    val currentSteps = (steps * progress).toInt()
    if (currentSteps < 2) return

    val segLen = config.maxLength / steps
    val path = Path()
    var x = cx
    var y = cy
    var angle = config.startAngle * PI.toFloat() / 180f

    path.moveTo(x, y)

    for (i in 0 until currentSteps) {
        val curve = config.curveDir * sin(i * 0.5f) * 0.15f
        angle += curve
        x += cos(angle) * segLen
        y += sin(angle) * segLen
        path.lineTo(x, y)

        if (i > 2 && i % 4 == 0 && progress > i.toFloat() / steps + 0.1f) {
            val leafAngle = angle + config.curveDir * PI.toFloat() / 3f
            val leafLen = segLen * 1.5f
            val leafX = x + cos(leafAngle) * leafLen
            val leafY = y + sin(leafAngle) * leafLen
            drawLine(
                color = GroSage.copy(alpha = 0.5f),
                start = Offset(x, y),
                end = Offset(leafX, leafY),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
            )
        }
    }

    drawPath(
        path = path,
        color = GroGreen.copy(alpha = 0.3f),
        style = Stroke(width = 3f, cap = StrokeCap.Round),
    )
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawVines(1f)
        }
        Text(
            text = "Gr\u014D",
            fontFamily = LoraFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 56.sp,
            color = GroGreen,
        )
    }
}

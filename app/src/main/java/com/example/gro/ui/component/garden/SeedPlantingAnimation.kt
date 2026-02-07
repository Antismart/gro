package com.example.gro.ui.component.garden

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.gro.ui.theme.GroOverlay
import kotlinx.coroutines.delay

@Composable
fun SeedPlantingAnimation(
    onAnimationComplete: () -> Unit,
) {
    // Seed drop: 0f = top, 1f = landed
    val seedProgress = remember { Animatable(0f) }
    // Sprout grow: 0f = nothing, 1f = fully sprouted
    val sproutProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Seed drops
        seedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing),
        )
        // Brief pause at landing
        delay(200)
        // Sprout emerges
        sproutProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing),
        )
        // Hold for a moment
        delay(400)
        onAnimationComplete()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(GroOverlay),
    ) {
        val cx = size.width / 2
        val groundY = size.height * 0.65f

        // Seed
        val seedY = size.height * 0.15f + (groundY - size.height * 0.15f) * seedProgress.value
        val seedSize = size.width * 0.035f

        if (seedProgress.value < 1f || sproutProgress.value < 0.3f) {
            drawCircle(
                color = Color(0xFF7C6955),
                radius = seedSize,
                center = Offset(cx, seedY),
            )
            // Seed highlight
            drawCircle(
                color = Color(0xFF9B8B7A),
                radius = seedSize * 0.5f,
                center = Offset(cx - seedSize * 0.2f, seedY - seedSize * 0.2f),
            )
        }

        // Soil splash on landing
        if (seedProgress.value >= 0.95f) {
            val splashAlpha = (1f - sproutProgress.value).coerceIn(0f, 1f)
            drawOval(
                color = Color(0xFF7C6955).copy(alpha = splashAlpha * 0.5f),
                topLeft = Offset(cx - size.width * 0.08f, groundY - size.height * 0.015f),
                size = Size(size.width * 0.16f, size.height * 0.03f),
            )
        }

        // Sprout
        if (sproutProgress.value > 0f) {
            val stemHeight = size.height * 0.12f * sproutProgress.value
            val stemColor = Color(0xFF4A7A4F)
            val leafColor = Color(0xFF2D5A3D)

            // Stem
            drawLine(
                color = stemColor,
                start = Offset(cx, groundY),
                end = Offset(cx, groundY - stemHeight),
                strokeWidth = size.width * 0.012f,
            )

            // Leaves appear after stem is halfway
            if (sproutProgress.value > 0.5f) {
                val leafAlpha = ((sproutProgress.value - 0.5f) / 0.5f)
                val leafSize = size.width * 0.04f * leafAlpha

                // Left leaf
                drawOval(
                    color = leafColor.copy(alpha = leafAlpha),
                    topLeft = Offset(cx - leafSize * 1.5f, groundY - stemHeight * 0.7f - leafSize * 0.5f),
                    size = Size(leafSize * 1.5f, leafSize),
                )
                // Right leaf
                drawOval(
                    color = leafColor.copy(alpha = leafAlpha),
                    topLeft = Offset(cx + leafSize * 0.2f, groundY - stemHeight * 0.55f - leafSize * 0.5f),
                    size = Size(leafSize * 1.3f, leafSize * 0.9f),
                )
            }
        }

        // "Planted!" text indicator - glow circle
        if (sproutProgress.value > 0.7f) {
            val glowAlpha = ((sproutProgress.value - 0.7f) / 0.3f) * 0.3f
            drawCircle(
                color = Color(0xFFE8B849).copy(alpha = glowAlpha),
                radius = size.width * 0.15f,
                center = Offset(cx, groundY - size.height * 0.1f),
            )
        }
    }
}

package com.example.gro.ui.component.garden

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.gro.domain.model.GardenWeather
import kotlin.math.sin
import kotlin.random.Random

private data class RainDrop(val x: Float, val speed: Float, val length: Float, val phase: Float)
private data class GoldenSparkle(val x: Float, val y: Float, val size: Float, val phase: Float)

@Composable
fun WeatherOverlay(
    weather: GardenWeather,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "weather")

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "weatherPhase",
    )

    val sparklePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sparklePulse",
    )

    val rainDrops = remember {
        val rng = Random(77)
        List(40) {
            RainDrop(
                x = rng.nextFloat(),
                speed = 0.6f + rng.nextFloat() * 0.4f,
                length = 0.02f + rng.nextFloat() * 0.03f,
                phase = rng.nextFloat(),
            )
        }
    }

    val goldenSparkles = remember {
        val rng = Random(123)
        List(20) {
            GoldenSparkle(
                x = rng.nextFloat(),
                y = 0.1f + rng.nextFloat() * 0.8f,
                size = 0.004f + rng.nextFloat() * 0.006f,
                phase = rng.nextFloat() * 6.2832f,
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        when (weather) {
            GardenWeather.RAINY -> {
                // Dark overlay
                drawRect(Color(0x18000020))
                // Rain drops
                rainDrops.forEach { drop ->
                    val dropY = ((drop.phase + phase * drop.speed) % 1.0f) * h * 1.2f - h * 0.1f
                    val dropX = drop.x * w + sin(dropY * 0.01f).toFloat() * w * 0.01f
                    val dropLen = drop.length * h
                    drawLine(
                        color = Color(0x40B0C4DE),
                        start = Offset(dropX, dropY),
                        end = Offset(dropX - w * 0.003f, dropY + dropLen),
                        strokeWidth = w * 0.002f,
                    )
                }
            }

            GardenWeather.CLOUDY -> {
                // Slight dim overlay
                drawRect(Color(0x10000010))
            }

            GardenWeather.PARTLY_CLOUDY -> {
                // No overlay — default scene clouds handle it
            }

            GardenWeather.SUNNY -> {
                // Sun rays from top-right
                val rayCenter = Offset(w * 0.85f, h * 0.02f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(
                            Color(0x18FFFDE7),
                            Color(0x08FFF9C4),
                            Color.Transparent,
                        ),
                        center = rayCenter,
                        radius = w * 0.6f,
                    ),
                    radius = w * 0.6f,
                    center = rayCenter,
                )
            }

            GardenWeather.GOLDEN_HOUR -> {
                // Warm golden wash
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0x15FFD700),
                            Color(0x08FFA500),
                            Color.Transparent,
                        ),
                    ),
                )
                // Floating golden sparkles
                goldenSparkles.forEach { sp ->
                    val alpha = (sin(sparklePhase + sp.phase) * 0.5f + 0.5f) * 0.7f
                    if (alpha > 0.15f) {
                        val cx = sp.x * w
                        val cy = sp.y * h + sin(sparklePhase * 0.5f + sp.phase) * h * 0.01f
                        val r = sp.size * w
                        drawCircle(Color(0xFFFFD700).copy(alpha = alpha), r, Offset(cx, cy))
                        drawCircle(Color(0xFFFFF8DC).copy(alpha = alpha * 0.4f), r * 2.5f, Offset(cx, cy))
                    }
                }
            }
        }
    }
}

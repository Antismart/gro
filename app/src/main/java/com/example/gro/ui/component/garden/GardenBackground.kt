package com.example.gro.ui.component.garden

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.gro.domain.model.GardenWeather
import kotlin.math.sin
import kotlin.random.Random

private data class CloudData(val x: Float, val y: Float, val w: Float, val h: Float, val speed: Float)
private data class GrassBladeData(val x: Float, val height: Float, val lean: Float)
private data class SparkleData(val x: Float, val y: Float, val size: Float, val phase: Float)

@Composable
fun GardenBackground(
    weather: GardenWeather = GardenWeather.SUNNY,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "bg")

    val cloudDrift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "cloudDrift",
    )

    val sparklePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sparkle",
    )

    val grassSway by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "grassSway",
    )

    val clouds = remember {
        listOf(
            CloudData(0.05f, 0.06f, 0.28f, 0.045f, 0.7f),
            CloudData(0.15f, 0.045f, 0.20f, 0.04f, 0.7f),
            CloudData(0.50f, 0.10f, 0.30f, 0.05f, 1.0f),
            CloudData(0.60f, 0.085f, 0.22f, 0.04f, 1.0f),
            CloudData(0.30f, 0.14f, 0.18f, 0.035f, 1.3f),
        )
    }

    val grassBlades = remember {
        val rng = Random(42)
        List(60) {
            GrassBladeData(
                x = rng.nextFloat(),
                height = 0.02f + rng.nextFloat() * 0.03f,
                lean = (rng.nextFloat() - 0.5f) * 0.015f,
            )
        }
    }

    val sparkles = remember {
        val rng = Random(99)
        List(12) {
            SparkleData(
                x = 0.05f + rng.nextFloat() * 0.9f,
                y = 0.58f + rng.nextFloat() * 0.25f,
                size = 0.003f + rng.nextFloat() * 0.004f,
                phase = rng.nextFloat() * 6.2832f,
            )
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawSky(w, h, weather)
        if (weather != GardenWeather.RAINY) {
            drawSun(w, h, weather == GardenWeather.GOLDEN_HOUR)
        }
        drawClouds(w, h, clouds, cloudDrift, weather)
        drawDistantHills(w, h)
        drawMidgroundHills(w, h)
        drawGrassland(w, h)
        drawGrassBlades(w, h, grassBlades, grassSway)
        drawSoil(w, h)
        drawSparkles(w, h, sparkles, sparklePhase)
    }
}

private fun DrawScope.drawSky(w: Float, h: Float, weather: GardenWeather) {
    val skyColors = when (weather) {
        GardenWeather.GOLDEN_HOUR -> listOf(
            Color(0xFFFDE8C8), Color(0xFFF5D6A0), Color(0xFFD4C4A0), Color(0xFFB8C8A0),
        )
        GardenWeather.RAINY -> listOf(
            Color(0xFF9AAFBF), Color(0xFFA0B8C0), Color(0xFF98B0A8), Color(0xFF88A898),
        )
        GardenWeather.CLOUDY -> listOf(
            Color(0xFFB0C8D8), Color(0xFFBBCED8), Color(0xFFAACDB5), Color(0xFF98C0A8),
        )
        else -> listOf(
            Color(0xFFC8E0F0), Color(0xFFD4E8F0), Color(0xFFBDD9C7), Color(0xFFA8D5BA),
        )
    }
    drawRect(
        brush = Brush.verticalGradient(
            colors = skyColors,
            startY = 0f,
            endY = h * 0.58f,
        ),
        size = Size(w, h * 0.58f),
    )
}

private fun DrawScope.drawSun(w: Float, h: Float, isGoldenHour: Boolean) {
    val sunCenter = Offset(w * 0.82f, h * 0.08f)
    val glowAlpha = if (isGoldenHour) 0.45f else 0.19f
    val discAlpha = if (isGoldenHour) 0.6f else 0.31f
    val glowColor = if (isGoldenHour) Color(0xFFFFD700) else Color(0xFFFFFDE7)
    // Outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                glowColor.copy(alpha = glowAlpha),
                Color(0xFFFFF9C4).copy(alpha = glowAlpha * 0.5f),
                Color.Transparent,
            ),
            center = sunCenter,
            radius = w * 0.18f,
        ),
        radius = w * 0.18f,
        center = sunCenter,
    )
    // Sun disc
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                glowColor.copy(alpha = discAlpha),
                Color(0xFFFFF59D).copy(alpha = discAlpha * 0.6f),
            ),
            center = sunCenter,
            radius = w * 0.05f,
        ),
        radius = w * 0.05f,
        center = sunCenter,
    )
}

private fun DrawScope.drawClouds(w: Float, h: Float, clouds: List<CloudData>, drift: Float, weather: GardenWeather) {
    val cloudAlpha = when (weather) {
        GardenWeather.RAINY -> 0.55f
        GardenWeather.CLOUDY -> 0.45f
        GardenWeather.GOLDEN_HOUR -> 0.2f
        else -> 0.25f
    }
    clouds.forEach { cloud ->
        val driftOffset = (drift * cloud.speed) % 1.4f - 0.2f
        val cx = ((cloud.x + driftOffset) % 1.3f) - 0.15f
        val color = Color(0xFFFFFFFF).copy(alpha = cloudAlpha)
        val colorInner = Color(0xFFFFFFFF).copy(alpha = cloudAlpha * 1.3f)

        // Main body
        drawRoundRect(
            color = color,
            topLeft = Offset(cx * w, cloud.y * h),
            size = Size(cloud.w * w, cloud.h * h),
            cornerRadius = CornerRadius(cloud.h * h * 0.5f),
        )
        // Brighter center lobe
        drawRoundRect(
            color = colorInner,
            topLeft = Offset((cx + cloud.w * 0.2f) * w, (cloud.y - cloud.h * 0.25f) * h),
            size = Size(cloud.w * 0.6f * w, cloud.h * 1.1f * h),
            cornerRadius = CornerRadius(cloud.h * h * 0.55f),
        )
    }
}

private fun DrawScope.drawDistantHills(w: Float, h: Float) {
    // Far hills — misty blue-green
    val farPath = Path().apply {
        moveTo(0f, h * 0.50f)
        cubicTo(w * 0.10f, h * 0.44f, w * 0.18f, h * 0.47f, w * 0.26f, h * 0.42f)
        cubicTo(w * 0.35f, h * 0.37f, w * 0.42f, h * 0.44f, w * 0.52f, h * 0.39f)
        cubicTo(w * 0.62f, h * 0.34f, w * 0.70f, h * 0.42f, w * 0.80f, h * 0.38f)
        cubicTo(w * 0.90f, h * 0.34f, w * 0.95f, h * 0.41f, w, h * 0.45f)
        lineTo(w, h * 0.56f)
        lineTo(0f, h * 0.56f)
        close()
    }
    drawPath(farPath, Color(0xFF6E9E78))

    // Nearer treeline — darker
    val treePath = Path().apply {
        moveTo(0f, h * 0.52f)
        cubicTo(w * 0.06f, h * 0.48f, w * 0.14f, h * 0.50f, w * 0.20f, h * 0.46f)
        cubicTo(w * 0.28f, h * 0.42f, w * 0.36f, h * 0.49f, w * 0.46f, h * 0.44f)
        cubicTo(w * 0.56f, h * 0.40f, w * 0.64f, h * 0.47f, w * 0.74f, h * 0.43f)
        cubicTo(w * 0.82f, h * 0.39f, w * 0.90f, h * 0.46f, w, h * 0.50f)
        lineTo(w, h * 0.58f)
        lineTo(0f, h * 0.58f)
        close()
    }
    drawPath(treePath, Color(0xFF5C7A5E))
}

private fun DrawScope.drawMidgroundHills(w: Float, h: Float) {
    val midPath = Path().apply {
        moveTo(0f, h * 0.55f)
        cubicTo(w * 0.12f, h * 0.52f, w * 0.25f, h * 0.56f, w * 0.40f, h * 0.53f)
        cubicTo(w * 0.55f, h * 0.50f, w * 0.70f, h * 0.55f, w * 0.85f, h * 0.52f)
        cubicTo(w * 0.92f, h * 0.51f, w * 0.96f, h * 0.54f, w, h * 0.53f)
        lineTo(w, h * 0.60f)
        lineTo(0f, h * 0.60f)
        close()
    }
    drawPath(midPath, Color(0xFF6B8F6D))
}

private fun DrawScope.drawGrassland(w: Float, h: Float) {
    val groundPath = Path().apply {
        moveTo(0f, h * 0.57f)
        cubicTo(w * 0.15f, h * 0.55f, w * 0.35f, h * 0.59f, w * 0.5f, h * 0.56f)
        cubicTo(w * 0.65f, h * 0.54f, w * 0.85f, h * 0.58f, w, h * 0.56f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    // Gradient grass: lighter at the horizon, richer toward camera
    drawPath(
        groundPath,
        Brush.verticalGradient(
            colors = listOf(Color(0xFF8FAE8B), Color(0xFF7A9E76), Color(0xFF6D8F68)),
            startY = h * 0.55f,
            endY = h * 0.85f,
        ),
    )
}

private fun DrawScope.drawGrassBlades(w: Float, h: Float, blades: List<GrassBladeData>, sway: Float) {
    val grassLine = h * 0.565f
    blades.forEach { blade ->
        val bx = blade.x * w
        val baseY = grassLine + blade.x * h * 0.02f // slight variation along horizon
        val tipLean = (blade.lean + sway * 0.008f * sin(blade.x * 20f)) * w
        val bladeH = blade.height * h

        val grassPath = Path().apply {
            moveTo(bx - w * 0.002f, baseY)
            cubicTo(bx - w * 0.001f, baseY - bladeH * 0.5f, bx + tipLean, baseY - bladeH * 0.8f, bx + tipLean, baseY - bladeH)
            cubicTo(bx + tipLean, baseY - bladeH * 0.8f, bx + w * 0.001f, baseY - bladeH * 0.5f, bx + w * 0.002f, baseY)
            close()
        }
        val alpha = 0.35f + blade.height * 5f
        drawPath(grassPath, Color(0xFF5A8A5C).copy(alpha = alpha.coerceAtMost(0.7f)))
    }
}

private fun DrawScope.drawSoil(w: Float, h: Float) {
    // Soil area with gradient
    val soilPath = Path().apply {
        moveTo(0f, h * 0.82f)
        cubicTo(w * 0.20f, h * 0.80f, w * 0.50f, h * 0.83f, w * 0.75f, h * 0.81f)
        cubicTo(w * 0.90f, h * 0.80f, w * 0.95f, h * 0.82f, w, h * 0.81f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        soilPath,
        Brush.verticalGradient(
            colors = listOf(Color(0xFF9B8B7A), Color(0xFF8A7A69), Color(0xFF7C6955)),
            startY = h * 0.80f,
            endY = h,
        ),
    )

    // Pebble details
    val pebbleColor = Color(0xFF6E5C4A).copy(alpha = 0.3f)
    drawCircle(pebbleColor, w * 0.008f, Offset(w * 0.15f, h * 0.88f))
    drawCircle(pebbleColor, w * 0.006f, Offset(w * 0.42f, h * 0.91f))
    drawCircle(pebbleColor, w * 0.009f, Offset(w * 0.68f, h * 0.86f))
    drawCircle(pebbleColor, w * 0.005f, Offset(w * 0.85f, h * 0.93f))
    drawCircle(pebbleColor, w * 0.007f, Offset(w * 0.30f, h * 0.95f))
}

private fun DrawScope.drawSparkles(w: Float, h: Float, sparkles: List<SparkleData>, phase: Float) {
    sparkles.forEach { sp ->
        val alpha = (sin(phase + sp.phase) * 0.5f + 0.5f) * 0.6f
        if (alpha > 0.1f) {
            val cx = sp.x * w
            val cy = sp.y * h
            val r = sp.size * w
            drawCircle(Color(0xFFFFFFFF).copy(alpha = alpha), r, Offset(cx, cy))
            drawCircle(Color(0xFFFFF9C4).copy(alpha = alpha * 0.5f), r * 2.5f, Offset(cx, cy))
        }
    }
}

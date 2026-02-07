package com.example.gro.ui.component.garden

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(val angle: Float, val speed: Float, val size: Float, val color: Color)

@Composable
fun SeedPlantingAnimation(
    onAnimationComplete: () -> Unit,
) {
    val seedDrop = remember { Animatable(0f) }
    val soilSplash = remember { Animatable(0f) }
    val sproutGrow = remember { Animatable(0f) }
    val glowPulse = remember { Animatable(0f) }
    val textFade = remember { Animatable(0f) }
    val overlayFade = remember { Animatable(1f) }

    val textMeasurer = rememberTextMeasurer()

    val particles = remember {
        val rng = Random(System.currentTimeMillis())
        List(16) {
            Particle(
                angle = rng.nextFloat() * 2f * PI.toFloat(),
                speed = 0.3f + rng.nextFloat() * 0.7f,
                size = 0.008f + rng.nextFloat() * 0.012f,
                color = listOf(
                    Color(0xFF9B8B7A),
                    Color(0xFF7C6955),
                    Color(0xFF8FAE8B),
                    Color(0xFFE8B849),
                ).random(rng),
            )
        }
    }

    LaunchedEffect(Unit) {
        // Seed drops with ease
        seedDrop.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        // Soil splash + particle burst
        soilSplash.animateTo(1f, tween(400, easing = LinearEasing))
        delay(100)
        // Sprout emerges with spring
        sproutGrow.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 200f))
        // Glow pulse
        glowPulse.animateTo(1f, tween(500))
        // Text fades in
        textFade.animateTo(1f, tween(400))
        delay(800)
        // Fade out everything
        overlayFade.animateTo(0f, tween(500))
        onAnimationComplete()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000).copy(alpha = 0.4f * overlayFade.value)),
    ) {
        val cx = size.width / 2
        val groundY = size.height * 0.62f
        val alpha = overlayFade.value

        // Seed trail sparkles
        if (seedDrop.value < 1f) {
            drawSeedTrail(cx, size.height * 0.12f, groundY, seedDrop.value, alpha)
        }

        // Seed
        val seedY = size.height * 0.12f + (groundY - size.height * 0.12f) * seedDrop.value
        if (seedDrop.value < 1f || sproutGrow.value < 0.2f) {
            val seedRadius = size.width * 0.035f
            // Glow behind seed
            drawCircle(
                color = Color(0xFFE8B849).copy(alpha = alpha * 0.2f),
                radius = seedRadius * 3f,
                center = Offset(cx, seedY),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF9B8B7A).copy(alpha = alpha), Color(0xFF6E5C4A).copy(alpha = alpha)),
                ),
                radius = seedRadius,
                center = Offset(cx, seedY),
            )
            // Highlight
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.3f),
                radius = seedRadius * 0.35f,
                center = Offset(cx - seedRadius * 0.25f, seedY - seedRadius * 0.25f),
            )
        }

        // Soil splash particles
        if (soilSplash.value > 0f) {
            drawSoilParticles(cx, groundY, particles, soilSplash.value, alpha)
        }

        // Soil mound
        if (seedDrop.value >= 0.95f) {
            val moundAlpha = (1f - (sproutGrow.value - 0.5f).coerceIn(0f, 0.5f)) * alpha
            drawOval(
                color = Color(0xFF7C6955).copy(alpha = moundAlpha * 0.6f),
                topLeft = Offset(cx - size.width * 0.1f, groundY - size.height * 0.01f),
                size = Size(size.width * 0.2f, size.height * 0.025f),
            )
        }

        // Sprout
        if (sproutGrow.value > 0f) {
            drawAnimatedSprout(cx, groundY, sproutGrow.value, alpha)
        }

        // Radial glow burst
        if (glowPulse.value > 0f) {
            val glowR = size.width * 0.25f * glowPulse.value
            val glowA = (1f - glowPulse.value * 0.5f) * alpha * 0.3f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color(0xFFE8B849).copy(alpha = glowA),
                        Color(0xFF8FAE8B).copy(alpha = glowA * 0.4f),
                        Color.Transparent,
                    ),
                ),
                radius = glowR,
                center = Offset(cx, groundY - size.height * 0.08f),
            )
        }

        // "Planted!" text
        if (textFade.value > 0f) {
            val textLayout = textMeasurer.measure(
                "Planted!",
                style = TextStyle(
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp,
                ),
            )
            drawText(
                textLayout,
                color = Color(0xFFF5F0E8).copy(alpha = textFade.value * alpha),
                topLeft = Offset(
                    cx - textLayout.size.width / 2,
                    groundY - size.height * 0.20f - textLayout.size.height / 2,
                ),
            )
        }
    }
}

private fun DrawScope.drawSeedTrail(cx: Float, startY: Float, endY: Float, progress: Float, alpha: Float) {
    val currentY = startY + (endY - startY) * progress
    val trailLength = (endY - startY) * 0.15f
    val trailTop = (currentY - trailLength).coerceAtLeast(startY)

    for (i in 0 until 5) {
        val t = i / 5f
        val ty = trailTop + (currentY - trailTop) * t
        val sparkleAlpha = (1f - t) * alpha * 0.4f
        if (sparkleAlpha > 0.05f) {
            drawCircle(
                color = Color(0xFFE8B849).copy(alpha = sparkleAlpha),
                radius = size.width * (0.006f + t * 0.004f),
                center = Offset(cx + sin(ty * 0.05f).toFloat() * size.width * 0.015f, ty),
            )
        }
    }
}

private fun DrawScope.drawSoilParticles(cx: Float, groundY: Float, particles: List<Particle>, progress: Float, alpha: Float) {
    val fadeOut = (1f - progress).coerceIn(0f, 1f)
    particles.forEach { p ->
        val dist = p.speed * progress * size.width * 0.18f
        val px = cx + cos(p.angle) * dist
        val py = groundY - sin(p.angle.coerceIn(0.2f, PI.toFloat() - 0.2f)) * dist + progress * size.height * 0.04f
        val pa = fadeOut * alpha * 0.8f
        if (pa > 0.05f) {
            drawCircle(
                color = p.color.copy(alpha = pa),
                radius = p.size * size.width * (1f - progress * 0.5f),
                center = Offset(px, py),
            )
        }
    }
}

private fun DrawScope.drawAnimatedSprout(cx: Float, groundY: Float, progress: Float, alpha: Float) {
    val stemHeight = size.height * 0.14f * progress
    val stemColor = Color(0xFF4A7A4F).copy(alpha = alpha)
    val leafColor = Color(0xFF2D5A3D).copy(alpha = alpha)

    // Stem
    drawLine(
        brush = Brush.verticalGradient(
            listOf(stemColor, Color(0xFF3A6A35).copy(alpha = alpha)),
            startY = groundY,
            endY = groundY - stemHeight,
        ),
        start = Offset(cx, groundY),
        end = Offset(cx, groundY - stemHeight),
        strokeWidth = size.width * 0.014f,
        cap = StrokeCap.Round,
    )

    // Leaves unfurl after stem is 40% grown
    if (progress > 0.4f) {
        val leafProgress = ((progress - 0.4f) / 0.6f).coerceIn(0f, 1f)
        val leafSize = size.width * 0.06f * leafProgress

        // Left leaf
        val leftPath = Path().apply {
            val lx = cx
            val ly = groundY - stemHeight * 0.7f
            moveTo(lx, ly)
            cubicTo(lx - leafSize * 0.5f, ly - leafSize * 0.5f, lx - leafSize, ly - leafSize * 0.1f, lx - leafSize * 0.8f, ly + leafSize * 0.1f)
            cubicTo(lx - leafSize * 0.4f, ly + leafSize * 0.3f, lx - leafSize * 0.1f, ly + leafSize * 0.1f, lx, ly)
            close()
        }
        drawPath(
            leftPath,
            Brush.linearGradient(
                listOf(Color(0xFF5AA05F).copy(alpha = alpha * leafProgress), leafColor.copy(alpha = alpha * leafProgress)),
            ),
        )

        // Right leaf
        val rightPath = Path().apply {
            val rx = cx
            val ry = groundY - stemHeight * 0.55f
            moveTo(rx, ry)
            cubicTo(rx + leafSize * 0.5f, ry - leafSize * 0.45f, rx + leafSize * 0.9f, ry - leafSize * 0.05f, rx + leafSize * 0.75f, ry + leafSize * 0.15f)
            cubicTo(rx + leafSize * 0.4f, ry + leafSize * 0.25f, rx + leafSize * 0.1f, ry + leafSize * 0.1f, rx, ry)
            close()
        }
        drawPath(
            rightPath,
            Brush.linearGradient(
                listOf(Color(0xFF5AA05F).copy(alpha = alpha * leafProgress), leafColor.copy(alpha = alpha * leafProgress)),
            ),
        )
    }
}

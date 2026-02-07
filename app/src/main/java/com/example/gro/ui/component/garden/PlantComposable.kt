package com.example.gro.ui.component.garden

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.HealthTier
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.PlantSpecies
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlantComposable(
    plant: Plant,
    cellSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "plantSway")
    val swayAngle by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2000 + (plant.id % 5) * 400).toInt(),
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sway",
    )

    val healthAlpha = when (plant.healthTier) {
        HealthTier.VIBRANT -> 1f
        HealthTier.SLIGHTLY_MUTED -> 0.8f
        HealthTier.WILTING -> 0.6f
        HealthTier.DORMANT -> 0.4f
    }

    Box(
        modifier = modifier
            .size(cellSize)
            .clickable(onClick = onClick),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawPlant(
                species = plant.species,
                stage = plant.growthStage,
                alpha = healthAlpha,
                swayAngle = swayAngle,
            )
        }
    }
}

private fun DrawScope.drawPlant(
    species: PlantSpecies,
    stage: GrowthStage,
    alpha: Float,
    swayAngle: Float,
) {
    val cx = size.width / 2
    val bottom = size.height * 0.85f

    // Soil mound
    drawOval(
        color = Color(0xFF7C6955).copy(alpha = alpha * 0.8f),
        topLeft = Offset(cx - size.width * 0.2f, bottom - size.height * 0.04f),
        size = Size(size.width * 0.4f, size.height * 0.08f),
    )

    when (stage) {
        GrowthStage.SEED -> drawSeed(cx, bottom, species, alpha)
        GrowthStage.SPROUT -> drawSprout(cx, bottom, species, alpha, swayAngle)
        GrowthStage.SAPLING -> drawSapling(cx, bottom, species, alpha, swayAngle)
        GrowthStage.MATURE -> drawMature(cx, bottom, species, alpha, swayAngle)
        GrowthStage.BLOOMING -> drawBlooming(cx, bottom, species, alpha, swayAngle)
    }
}

private fun DrawScope.drawSeed(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float) {
    val color = speciesColor(species).copy(alpha = alpha)
    // Seed shape
    drawOval(
        color = color.copy(alpha = alpha * 0.7f),
        topLeft = Offset(cx - size.width * 0.04f, bottom - size.height * 0.06f),
        size = Size(size.width * 0.08f, size.height * 0.05f),
    )
}

private fun DrawScope.drawSprout(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    val color = speciesColor(species).copy(alpha = alpha)
    val stemHeight = size.height * 0.2f

    rotate(sway, pivot = Offset(cx, bottom)) {
        // Stem
        drawLine(
            color = Color(0xFF4A7A4F).copy(alpha = alpha),
            start = Offset(cx, bottom),
            end = Offset(cx, bottom - stemHeight),
            strokeWidth = size.width * 0.025f,
        )
        // Two small leaves
        drawLeaf(cx - size.width * 0.06f, bottom - stemHeight * 0.7f, size.width * 0.08f, color, true)
        drawLeaf(cx + size.width * 0.06f, bottom - stemHeight * 0.6f, size.width * 0.07f, color, false)
    }
}

private fun DrawScope.drawSapling(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    val color = speciesColor(species).copy(alpha = alpha)
    val stemHeight = size.height * 0.35f

    rotate(sway, pivot = Offset(cx, bottom)) {
        // Stem
        drawLine(
            color = Color(0xFF4A7A4F).copy(alpha = alpha),
            start = Offset(cx, bottom),
            end = Offset(cx, bottom - stemHeight),
            strokeWidth = size.width * 0.03f,
        )
        // Multiple leaves based on species
        when (species) {
            PlantSpecies.SOL -> {
                // Fern fronds
                for (i in 0 until 4) {
                    val y = bottom - stemHeight * (0.4f + i * 0.15f)
                    drawLeaf(cx - size.width * 0.1f, y, size.width * 0.12f, color, true)
                    drawLeaf(cx + size.width * 0.1f, y, size.width * 0.11f, color, false)
                }
            }
            PlantSpecies.BONK -> {
                // Cactus arms
                drawOval(color, Offset(cx - size.width * 0.08f, bottom - stemHeight * 0.8f), Size(size.width * 0.16f, stemHeight * 0.6f))
                // Spines
                val spineColor = Color(0xFFB8D4A8).copy(alpha = alpha)
                for (angle in listOf(-30f, 0f, 30f, 60f, -60f)) {
                    val rad = Math.toRadians(angle.toDouble())
                    val spineLen = size.width * 0.04f
                    val spineY = bottom - stemHeight * 0.6f
                    drawLine(spineColor, Offset(cx, spineY), Offset(cx + (cos(rad) * spineLen).toFloat(), spineY - (sin(rad) * spineLen).toFloat()), size.width * 0.01f)
                }
            }
            PlantSpecies.RAY -> {
                // Sunflower stem + small bud
                drawLeaf(cx - size.width * 0.08f, bottom - stemHeight * 0.5f, size.width * 0.1f, color, true)
                drawLeaf(cx + size.width * 0.08f, bottom - stemHeight * 0.65f, size.width * 0.09f, color, false)
                drawCircle(Color(0xFFE8B849).copy(alpha = alpha * 0.6f), size.width * 0.05f, Offset(cx, bottom - stemHeight))
            }
            else -> {
                // Generic leaves
                drawLeaf(cx - size.width * 0.1f, bottom - stemHeight * 0.5f, size.width * 0.12f, color, true)
                drawLeaf(cx + size.width * 0.1f, bottom - stemHeight * 0.65f, size.width * 0.11f, color, false)
                drawLeaf(cx - size.width * 0.08f, bottom - stemHeight * 0.8f, size.width * 0.1f, color, true)
            }
        }
    }
}

private fun DrawScope.drawMature(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    val color = speciesColor(species).copy(alpha = alpha)
    val stemHeight = size.height * 0.5f

    rotate(sway, pivot = Offset(cx, bottom)) {
        when (species) {
            PlantSpecies.SOL -> {
                // Full fern with many fronds
                drawLine(Color(0xFF4A7A4F).copy(alpha = alpha), Offset(cx, bottom), Offset(cx, bottom - stemHeight), size.width * 0.035f)
                for (i in 0 until 6) {
                    val y = bottom - stemHeight * (0.3f + i * 0.11f)
                    val leafSize = size.width * (0.15f - i * 0.01f)
                    drawLeaf(cx - size.width * 0.12f, y, leafSize, color, true)
                    drawLeaf(cx + size.width * 0.12f, y, leafSize, color, false)
                }
            }
            PlantSpecies.USDC -> {
                // Broad symmetrical plant
                drawLine(Color(0xFF4A7A4F).copy(alpha = alpha), Offset(cx, bottom), Offset(cx, bottom - stemHeight), size.width * 0.04f)
                for (i in 0 until 4) {
                    val y = bottom - stemHeight * (0.35f + i * 0.15f)
                    val leafSize = size.width * (0.18f - i * 0.02f)
                    drawLeaf(cx - size.width * 0.14f, y, leafSize, color, true)
                    drawLeaf(cx + size.width * 0.14f, y, leafSize, color, false)
                }
            }
            PlantSpecies.BONK -> {
                // Full cactus
                drawOval(color, Offset(cx - size.width * 0.1f, bottom - stemHeight * 0.85f), Size(size.width * 0.2f, stemHeight * 0.7f))
                // Arms
                drawOval(color, Offset(cx - size.width * 0.22f, bottom - stemHeight * 0.65f), Size(size.width * 0.1f, stemHeight * 0.25f))
                drawOval(color, Offset(cx + size.width * 0.12f, bottom - stemHeight * 0.55f), Size(size.width * 0.1f, stemHeight * 0.2f))
            }
            PlantSpecies.JUP -> {
                // Vine curling upward
                val vinePath = Path().apply {
                    moveTo(cx, bottom)
                    cubicTo(cx - size.width * 0.15f, bottom - stemHeight * 0.3f, cx + size.width * 0.15f, bottom - stemHeight * 0.6f, cx, bottom - stemHeight)
                }
                drawPath(vinePath, Color(0xFF4A7A4F).copy(alpha = alpha))
                // Leaves along vine
                for (i in 0 until 5) {
                    val t = (i + 1) / 6f
                    val leafX = cx + sin(t * Math.PI * 2).toFloat() * size.width * 0.1f
                    val leafY = bottom - stemHeight * t
                    drawLeaf(leafX, leafY, size.width * 0.09f, color, i % 2 == 0)
                }
            }
            PlantSpecies.RAY -> {
                // Tall sunflower
                drawLine(Color(0xFF4A7A4F).copy(alpha = alpha), Offset(cx, bottom), Offset(cx, bottom - stemHeight), size.width * 0.04f)
                drawLeaf(cx - size.width * 0.1f, bottom - stemHeight * 0.4f, size.width * 0.14f, color, true)
                drawLeaf(cx + size.width * 0.1f, bottom - stemHeight * 0.55f, size.width * 0.13f, color, false)
                // Flower head
                val headCenter = Offset(cx, bottom - stemHeight)
                drawCircle(Color(0xFFE8B849).copy(alpha = alpha), size.width * 0.1f, headCenter)
                drawCircle(Color(0xFF7C6955).copy(alpha = alpha * 0.8f), size.width * 0.055f, headCenter)
            }
            PlantSpecies.ORCA -> {
                // Low broad lily
                drawLine(Color(0xFF4A7A4F).copy(alpha = alpha), Offset(cx, bottom), Offset(cx, bottom - stemHeight * 0.7f), size.width * 0.03f)
                // Broad leaves
                val lilyColor = Color(0xFF7EACC1).copy(alpha = alpha)
                drawOval(lilyColor, Offset(cx - size.width * 0.18f, bottom - stemHeight * 0.5f), Size(size.width * 0.16f, stemHeight * 0.15f))
                drawOval(lilyColor, Offset(cx + size.width * 0.04f, bottom - stemHeight * 0.55f), Size(size.width * 0.17f, stemHeight * 0.14f))
                drawOval(color, Offset(cx - size.width * 0.12f, bottom - stemHeight * 0.65f), Size(size.width * 0.15f, stemHeight * 0.12f))
            }
        }
    }
}

private fun DrawScope.drawBlooming(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    // Draw mature plant first
    drawMature(cx, bottom, species, alpha, sway)

    // Add blooms on top
    rotate(sway, pivot = Offset(cx, bottom)) {
        val stemHeight = size.height * 0.5f
        val bloomColor = when (species) {
            PlantSpecies.SOL -> Color(0xFF8FAE8B)
            PlantSpecies.USDC -> Color(0xFFB8D4A8)
            PlantSpecies.BONK -> Color(0xFFD4726A)
            PlantSpecies.JUP -> Color(0xFF9B7ED4)
            PlantSpecies.RAY -> Color(0xFFE8B849)
            PlantSpecies.ORCA -> Color(0xFFD4E8F0)
        }.copy(alpha = alpha)

        // Flower buds at top
        drawCircle(bloomColor, size.width * 0.04f, Offset(cx - size.width * 0.06f, bottom - stemHeight * 0.95f))
        drawCircle(bloomColor, size.width * 0.05f, Offset(cx + size.width * 0.04f, bottom - stemHeight * 1.02f))
        drawCircle(bloomColor, size.width * 0.035f, Offset(cx, bottom - stemHeight * 1.08f))

        // Sparkle particles for blooming
        val sparkle = Color(0xFFE8B849).copy(alpha = alpha * 0.6f)
        drawCircle(sparkle, size.width * 0.015f, Offset(cx - size.width * 0.15f, bottom - stemHeight * 0.85f))
        drawCircle(sparkle, size.width * 0.012f, Offset(cx + size.width * 0.18f, bottom - stemHeight * 0.9f))
        drawCircle(sparkle, size.width * 0.01f, Offset(cx + size.width * 0.08f, bottom - stemHeight * 1.1f))
    }
}

private fun DrawScope.drawLeaf(x: Float, y: Float, leafSize: Float, color: Color, pointLeft: Boolean) {
    val path = Path().apply {
        if (pointLeft) {
            moveTo(x, y)
            cubicTo(x - leafSize * 0.6f, y - leafSize * 0.4f, x - leafSize, y - leafSize * 0.1f, x - leafSize * 0.8f, y + leafSize * 0.1f)
            cubicTo(x - leafSize * 0.5f, y + leafSize * 0.3f, x - leafSize * 0.2f, y + leafSize * 0.1f, x, y)
        } else {
            moveTo(x, y)
            cubicTo(x + leafSize * 0.6f, y - leafSize * 0.4f, x + leafSize, y - leafSize * 0.1f, x + leafSize * 0.8f, y + leafSize * 0.1f)
            cubicTo(x + leafSize * 0.5f, y + leafSize * 0.3f, x + leafSize * 0.2f, y + leafSize * 0.1f, x, y)
        }
        close()
    }
    drawPath(path, color)
}

private fun speciesColor(species: PlantSpecies): Color = when (species) {
    PlantSpecies.SOL -> Color(0xFF2D5A3D)
    PlantSpecies.USDC -> Color(0xFF3D7A53)
    PlantSpecies.BONK -> Color(0xFF8FAE8B)
    PlantSpecies.JUP -> Color(0xFF5C7A5E)
    PlantSpecies.RAY -> Color(0xFF4A8A4F)
    PlantSpecies.ORCA -> Color(0xFF5A8A7A)
}

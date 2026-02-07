package com.example.gro.ui.component.garden

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.HealthTier
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.PlantSpecies
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlantComposable(
    plant: Plant,
    cellSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "plant_${plant.id}")

    // Sway animation — unique period per plant
    val swayAngle by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200 + (plant.id % 7).toInt() * 300,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sway_${plant.id}",
    )

    // Subtle breathing scale for healthy plants
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (plant.healthTier == HealthTier.VIBRANT) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath_${plant.id}",
    )

    // Bloom sparkle phase
    val sparklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sparkle_${plant.id}",
    )

    // Pop-in entrance animation
    val entranceScale = remember { Animatable(0f) }
    LaunchedEffect(plant.id) {
        entranceScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        )
    }

    val healthAlpha = when (plant.healthTier) {
        HealthTier.VIBRANT -> 1f
        HealthTier.SLIGHTLY_MUTED -> 0.82f
        HealthTier.WILTING -> 0.6f
        HealthTier.DORMANT -> 0.4f
    }

    Box(
        modifier = modifier
            .size(cellSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            scale(
                scale = entranceScale.value * breathScale,
                pivot = Offset(size.width / 2, size.height * 0.85f),
            ) {
                drawPlant(
                    species = plant.species,
                    stage = plant.growthStage,
                    alpha = healthAlpha,
                    swayAngle = swayAngle,
                    sparklePhase = if (plant.growthStage == GrowthStage.BLOOMING) sparklePhase else 0f,
                )
            }
        }
    }
}

private fun DrawScope.drawPlant(
    species: PlantSpecies,
    stage: GrowthStage,
    alpha: Float,
    swayAngle: Float,
    sparklePhase: Float,
) {
    val cx = size.width / 2
    val bottom = size.height * 0.85f

    // Ground shadow
    drawOval(
        color = Color(0xFF000000).copy(alpha = alpha * 0.12f),
        topLeft = Offset(cx - size.width * 0.22f, bottom - size.height * 0.01f),
        size = Size(size.width * 0.44f, size.height * 0.05f),
    )

    // Soil mound with gradient
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF9B8B7A).copy(alpha = alpha * 0.9f),
                Color(0xFF7C6955).copy(alpha = alpha * 0.7f),
            ),
        ),
        topLeft = Offset(cx - size.width * 0.2f, bottom - size.height * 0.04f),
        size = Size(size.width * 0.4f, size.height * 0.08f),
    )

    when (stage) {
        GrowthStage.SEED -> drawSeed(cx, bottom, species, alpha)
        GrowthStage.SPROUT -> drawSprout(cx, bottom, species, alpha, swayAngle)
        GrowthStage.SAPLING -> drawSapling(cx, bottom, species, alpha, swayAngle)
        GrowthStage.MATURE -> drawMature(cx, bottom, species, alpha, swayAngle)
        GrowthStage.BLOOMING -> {
            drawBlooming(cx, bottom, species, alpha, swayAngle)
            if (sparklePhase > 0f) drawBloomSparkles(cx, bottom, species, alpha, sparklePhase)
        }
    }
}

// --- SEED ---
private fun DrawScope.drawSeed(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float) {
    val colors = speciesColors(species)
    val seedW = size.width * 0.07f
    val seedH = size.height * 0.05f
    val seedY = bottom - size.height * 0.055f

    // Seed body
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                colors.dark.copy(alpha = alpha * 0.85f),
                colors.mid.copy(alpha = alpha * 0.6f),
            ),
            center = Offset(cx, seedY + seedH / 2),
            radius = seedW,
        ),
        topLeft = Offset(cx - seedW / 2, seedY),
        size = Size(seedW, seedH),
    )
    // Seed highlight
    drawOval(
        color = Color.White.copy(alpha = alpha * 0.2f),
        topLeft = Offset(cx - seedW * 0.2f, seedY + seedH * 0.15f),
        size = Size(seedW * 0.35f, seedH * 0.35f),
    )
}

// --- SPROUT ---
private fun DrawScope.drawSprout(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    val colors = speciesColors(species)
    val stemH = size.height * 0.22f

    rotate(sway, pivot = Offset(cx, bottom)) {
        // Stem with gradient
        drawStem(cx, bottom, stemH, alpha, size.width * 0.022f)

        // Two cotyledon leaves
        val leafSize = size.width * 0.10f
        drawGradientLeaf(cx - size.width * 0.02f, bottom - stemH * 0.65f, leafSize, colors, alpha, true)
        drawGradientLeaf(cx + size.width * 0.02f, bottom - stemH * 0.5f, leafSize * 0.9f, colors, alpha, false)

        // Tiny bud at tip
        drawCircle(
            color = colors.light.copy(alpha = alpha * 0.6f),
            radius = size.width * 0.018f,
            center = Offset(cx, bottom - stemH),
        )
    }
}

// --- SAPLING ---
private fun DrawScope.drawSapling(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    val colors = speciesColors(species)
    val stemH = size.height * 0.38f

    rotate(sway, pivot = Offset(cx, bottom)) {
        drawStem(cx, bottom, stemH, alpha, size.width * 0.028f)

        when (species) {
            PlantSpecies.SOL -> {
                for (i in 0 until 5) {
                    val y = bottom - stemH * (0.35f + i * 0.13f)
                    val s = size.width * (0.14f - i * 0.008f)
                    drawGradientLeaf(cx - size.width * 0.03f, y, s, colors, alpha, true)
                    drawGradientLeaf(cx + size.width * 0.03f, y + stemH * 0.04f, s * 0.95f, colors, alpha, false)
                }
            }
            PlantSpecies.BONK -> {
                // Cactus body
                val bodyW = size.width * 0.18f
                val bodyH = stemH * 0.55f
                drawCactusBody(cx, bottom - stemH * 0.3f, bodyW, bodyH, colors, alpha)
                drawCactusSpines(cx, bottom - stemH * 0.5f, bodyW, alpha)
            }
            PlantSpecies.RAY -> {
                drawGradientLeaf(cx - size.width * 0.04f, bottom - stemH * 0.45f, size.width * 0.13f, colors, alpha, true)
                drawGradientLeaf(cx + size.width * 0.04f, bottom - stemH * 0.6f, size.width * 0.12f, colors, alpha, false)
                // Small flower bud
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFE8B849).copy(alpha = alpha), Color(0xFFD4A040).copy(alpha = alpha * 0.7f)),
                    ),
                    radius = size.width * 0.045f,
                    center = Offset(cx, bottom - stemH),
                )
            }
            else -> {
                for (i in 0 until 3) {
                    val y = bottom - stemH * (0.4f + i * 0.18f)
                    val s = size.width * (0.14f - i * 0.01f)
                    drawGradientLeaf(cx - size.width * 0.03f, y, s, colors, alpha, true)
                    drawGradientLeaf(cx + size.width * 0.03f, y + stemH * 0.06f, s * 0.9f, colors, alpha, false)
                }
            }
        }
    }
}

// --- MATURE ---
private fun DrawScope.drawMature(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    val colors = speciesColors(species)
    val stemH = size.height * 0.52f

    rotate(sway, pivot = Offset(cx, bottom)) {
        when (species) {
            PlantSpecies.SOL -> {
                drawStem(cx, bottom, stemH, alpha, size.width * 0.032f)
                for (i in 0 until 7) {
                    val y = bottom - stemH * (0.25f + i * 0.1f)
                    val s = size.width * (0.17f - i * 0.01f)
                    drawGradientLeaf(cx - size.width * 0.04f, y, s, colors, alpha, true)
                    drawGradientLeaf(cx + size.width * 0.04f, y + stemH * 0.03f, s * 0.95f, colors, alpha, false)
                }
            }
            PlantSpecies.USDC -> {
                drawStem(cx, bottom, stemH, alpha, size.width * 0.035f)
                for (i in 0 until 5) {
                    val y = bottom - stemH * (0.3f + i * 0.14f)
                    val s = size.width * (0.20f - i * 0.015f)
                    drawGradientLeaf(cx - size.width * 0.05f, y, s, colors, alpha, true)
                    drawGradientLeaf(cx + size.width * 0.05f, y + stemH * 0.04f, s, colors, alpha, false)
                }
            }
            PlantSpecies.BONK -> {
                val bodyW = size.width * 0.22f
                val bodyH = stemH * 0.65f
                drawCactusBody(cx, bottom - stemH * 0.25f, bodyW, bodyH, colors, alpha)
                // Arms
                drawCactusBody(cx - size.width * 0.18f, bottom - stemH * 0.55f, bodyW * 0.45f, bodyH * 0.35f, colors, alpha)
                drawCactusBody(cx + size.width * 0.16f, bottom - stemH * 0.45f, bodyW * 0.45f, bodyH * 0.3f, colors, alpha)
                drawCactusSpines(cx, bottom - stemH * 0.55f, bodyW * 1.2f, alpha)
            }
            PlantSpecies.JUP -> {
                // Curling vine
                val vinePath = Path().apply {
                    moveTo(cx, bottom)
                    cubicTo(
                        cx - size.width * 0.18f, bottom - stemH * 0.3f,
                        cx + size.width * 0.18f, bottom - stemH * 0.6f,
                        cx - size.width * 0.05f, bottom - stemH,
                    )
                }
                drawPath(
                    vinePath,
                    Brush.verticalGradient(
                        listOf(Color(0xFF5C7A4F).copy(alpha = alpha), Color(0xFF4A6A3F).copy(alpha = alpha)),
                        startY = bottom,
                        endY = bottom - stemH,
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(size.width * 0.03f, cap = StrokeCap.Round),
                )
                for (i in 0 until 6) {
                    val t = (i + 1) / 7f
                    val leafX = cx + sin(t * PI * 2.5).toFloat() * size.width * 0.12f
                    val leafY = bottom - stemH * t
                    drawGradientLeaf(leafX, leafY, size.width * 0.11f, colors, alpha, i % 2 == 0)
                }
            }
            PlantSpecies.RAY -> {
                drawStem(cx, bottom, stemH, alpha, size.width * 0.035f)
                drawGradientLeaf(cx - size.width * 0.05f, bottom - stemH * 0.35f, size.width * 0.16f, colors, alpha, true)
                drawGradientLeaf(cx + size.width * 0.05f, bottom - stemH * 0.50f, size.width * 0.15f, colors, alpha, false)
                // Sunflower head
                drawSunflowerHead(cx, bottom - stemH, size.width * 0.12f, alpha)
            }
            PlantSpecies.ORCA -> {
                drawStem(cx, bottom, stemH * 0.7f, alpha, size.width * 0.025f)
                // Broad water lily pads
                val padColor = Brush.radialGradient(
                    listOf(colors.light.copy(alpha = alpha), colors.mid.copy(alpha = alpha * 0.8f)),
                )
                drawOval(padColor, Offset(cx - size.width * 0.22f, bottom - stemH * 0.42f), Size(size.width * 0.2f, stemH * 0.12f))
                drawOval(padColor, Offset(cx + size.width * 0.04f, bottom - stemH * 0.48f), Size(size.width * 0.22f, stemH * 0.11f))
                drawOval(
                    Brush.radialGradient(listOf(colors.mid.copy(alpha = alpha), colors.dark.copy(alpha = alpha * 0.7f))),
                    Offset(cx - size.width * 0.14f, bottom - stemH * 0.56f),
                    Size(size.width * 0.18f, stemH * 0.10f),
                )
                // Blossom
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color(0xFFD4E8F0).copy(alpha = alpha), Color(0xFFB0D0E0).copy(alpha = alpha * 0.6f))),
                    radius = size.width * 0.055f,
                    center = Offset(cx, bottom - stemH * 0.65f),
                )
            }
        }
    }
}

// --- BLOOMING ---
private fun DrawScope.drawBlooming(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, sway: Float) {
    drawMature(cx, bottom, species, alpha, sway)

    rotate(sway, pivot = Offset(cx, bottom)) {
        val stemH = size.height * 0.52f
        val bloomColors = speciesBloomColors(species)

        // Flower buds scattered at canopy
        bloomColors.forEach { color ->
            val c = color.copy(alpha = alpha * 0.9f)
            drawCircle(c, size.width * 0.035f, Offset(cx - size.width * 0.08f, bottom - stemH * 0.92f))
            drawCircle(c, size.width * 0.042f, Offset(cx + size.width * 0.06f, bottom - stemH * 1.0f))
            drawCircle(c, size.width * 0.03f, Offset(cx - size.width * 0.02f, bottom - stemH * 1.06f))
        }
    }
}

private fun DrawScope.drawBloomSparkles(cx: Float, bottom: Float, species: PlantSpecies, alpha: Float, phase: Float) {
    val stemH = size.height * 0.52f
    val sparkleColor = Color(0xFFE8B849)
    val offsets = listOf(
        Offset(-0.16f, -0.82f), Offset(0.18f, -0.90f), Offset(0.10f, -1.08f),
        Offset(-0.10f, -1.05f), Offset(-0.20f, -0.95f), Offset(0.15f, -0.78f),
    )
    offsets.forEachIndexed { i, off ->
        val a = (sin(phase + i * 1.1f) * 0.5f + 0.5f) * alpha * 0.7f
        if (a > 0.1f) {
            val px = cx + off.x * size.width
            val py = bottom + off.y * stemH
            drawCircle(sparkleColor.copy(alpha = a), size.width * 0.012f, Offset(px, py))
            drawCircle(sparkleColor.copy(alpha = a * 0.4f), size.width * 0.025f, Offset(px, py))
        }
    }
}

// --- Drawing helpers ---

private fun DrawScope.drawStem(cx: Float, bottom: Float, height: Float, alpha: Float, width: Float) {
    drawLine(
        brush = Brush.verticalGradient(
            listOf(Color(0xFF5C8A4F).copy(alpha = alpha), Color(0xFF3A6A35).copy(alpha = alpha)),
            startY = bottom,
            endY = bottom - height,
        ),
        start = Offset(cx, bottom),
        end = Offset(cx, bottom - height),
        strokeWidth = width,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawGradientLeaf(x: Float, y: Float, leafSize: Float, colors: PlantColors, alpha: Float, pointLeft: Boolean) {
    val dir = if (pointLeft) -1f else 1f
    val path = Path().apply {
        moveTo(x, y)
        cubicTo(
            x + dir * leafSize * 0.5f, y - leafSize * 0.45f,
            x + dir * leafSize * 0.9f, y - leafSize * 0.15f,
            x + dir * leafSize * 0.75f, y + leafSize * 0.12f,
        )
        cubicTo(
            x + dir * leafSize * 0.5f, y + leafSize * 0.3f,
            x + dir * leafSize * 0.2f, y + leafSize * 0.12f,
            x, y,
        )
        close()
    }
    drawPath(
        path,
        Brush.linearGradient(
            colors = listOf(colors.light.copy(alpha = alpha), colors.dark.copy(alpha = alpha * 0.85f)),
            start = Offset(x, y - leafSize * 0.3f),
            end = Offset(x + dir * leafSize * 0.7f, y + leafSize * 0.2f),
        ),
    )
    // Leaf vein
    drawLine(
        color = colors.dark.copy(alpha = alpha * 0.25f),
        start = Offset(x, y),
        end = Offset(x + dir * leafSize * 0.55f, y + leafSize * 0.02f),
        strokeWidth = size.width * 0.004f,
    )
}

private fun DrawScope.drawCactusBody(cx: Float, cy: Float, bodyW: Float, bodyH: Float, colors: PlantColors, alpha: Float) {
    drawOval(
        brush = Brush.horizontalGradient(
            listOf(colors.dark.copy(alpha = alpha), colors.light.copy(alpha = alpha), colors.dark.copy(alpha = alpha * 0.9f)),
            startX = cx - bodyW / 2,
            endX = cx + bodyW / 2,
        ),
        topLeft = Offset(cx - bodyW / 2, cy - bodyH / 2),
        size = Size(bodyW, bodyH),
    )
    // Highlight ridge
    drawLine(
        color = Color.White.copy(alpha = alpha * 0.15f),
        start = Offset(cx, cy - bodyH * 0.4f),
        end = Offset(cx, cy + bodyH * 0.35f),
        strokeWidth = size.width * 0.008f,
    )
}

private fun DrawScope.drawCactusSpines(cx: Float, cy: Float, spread: Float, alpha: Float) {
    val spineColor = Color(0xFFD0D8C0).copy(alpha = alpha * 0.5f)
    for (angle in listOf(-50f, -20f, 10f, 40f, 70f, -70f)) {
        val rad = Math.toRadians(angle.toDouble())
        val len = size.width * 0.035f
        drawLine(
            spineColor,
            Offset(cx + cos(rad).toFloat() * spread * 0.35f, cy + sin(rad).toFloat() * spread * 0.2f),
            Offset(cx + cos(rad).toFloat() * (spread * 0.35f + len), cy + sin(rad).toFloat() * (spread * 0.2f + len * 0.5f)),
            size.width * 0.006f,
        )
    }
}

private fun DrawScope.drawSunflowerHead(cx: Float, cy: Float, radius: Float, alpha: Float) {
    // Petals
    val petalColor = Color(0xFFE8B849).copy(alpha = alpha)
    for (i in 0 until 10) {
        val angle = (i * 36f) * PI.toFloat() / 180f
        val px = cx + cos(angle) * radius * 1.1f
        val py = cy + sin(angle) * radius * 1.1f
        drawOval(
            color = petalColor,
            topLeft = Offset(px - radius * 0.22f, py - radius * 0.4f),
            size = Size(radius * 0.44f, radius * 0.8f),
        )
    }
    // Center disc
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFF5C4A28).copy(alpha = alpha), Color(0xFF7C6955).copy(alpha = alpha * 0.9f)),
        ),
        radius = radius * 0.55f,
        center = Offset(cx, cy),
    )
    // Center highlight
    drawCircle(
        color = Color(0xFFE8B849).copy(alpha = alpha * 0.2f),
        radius = radius * 0.25f,
        center = Offset(cx - radius * 0.1f, cy - radius * 0.1f),
    )
}

// --- Color system ---

private data class PlantColors(val light: Color, val mid: Color, val dark: Color)

private fun speciesColors(species: PlantSpecies): PlantColors = when (species) {
    PlantSpecies.SOL -> PlantColors(Color(0xFF5AA05F), Color(0xFF2D5A3D), Color(0xFF1E4030))
    PlantSpecies.USDC -> PlantColors(Color(0xFF6AC075), Color(0xFF3D7A53), Color(0xFF2A5A3A))
    PlantSpecies.BONK -> PlantColors(Color(0xFFB0D4A0), Color(0xFF8FAE8B), Color(0xFF6A8866))
    PlantSpecies.JUP -> PlantColors(Color(0xFF80AA70), Color(0xFF5C7A5E), Color(0xFF3E5A40))
    PlantSpecies.RAY -> PlantColors(Color(0xFF68B060), Color(0xFF4A8A4F), Color(0xFF306838))
    PlantSpecies.ORCA -> PlantColors(Color(0xFF80BBA8), Color(0xFF5A8A7A), Color(0xFF3A6A5A))
}

private fun speciesBloomColors(species: PlantSpecies): List<Color> = when (species) {
    PlantSpecies.SOL -> listOf(Color(0xFFA8D5BA))
    PlantSpecies.USDC -> listOf(Color(0xFFC8E8D0))
    PlantSpecies.BONK -> listOf(Color(0xFFD4726A))
    PlantSpecies.JUP -> listOf(Color(0xFF9B7ED4))
    PlantSpecies.RAY -> listOf(Color(0xFFE8B849))
    PlantSpecies.ORCA -> listOf(Color(0xFFD4E8F0))
}

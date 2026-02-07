package com.example.gro.ui.component.garden

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import com.example.gro.domain.model.Plant
import kotlin.random.Random

@Composable
fun GardenScene(
    plants: List<Plant>,
    onPlantClick: (Plant) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Scene-level fade-in
    val sceneAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        sceneAlpha.animateTo(1f, tween(600))
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().alpha(sceneAlpha.value)) {
        val density = LocalDensity.current
        val sceneWidthPx = with(density) { maxWidth.toPx() }
        val sceneHeightPx = with(density) { maxHeight.toPx() }

        // Background layers
        GardenBackground(modifier = Modifier.fillMaxSize())

        // Plant grid: 4 columns x 3 rows
        // Plants placed in the bottom 40% of the scene (ground area)
        val gridColumns = 4
        val gridRows = 3
        val plantAreaTop = sceneHeightPx * 0.55f
        val plantAreaHeight = sceneHeightPx * 0.38f
        val cellWidth = sceneWidthPx / gridColumns
        val cellHeight = plantAreaHeight / gridRows

        // Sort by row so back-row plants draw first (painter's algorithm)
        val sortedPlants = remember(plants) {
            plants.sortedBy { it.gridPositionY }
        }

        sortedPlants.forEachIndexed { index, plant ->
            val random = remember(plant.id) { Random(plant.id) }
            val jitterX = (random.nextFloat() - 0.5f) * cellWidth * 0.18f
            val jitterY = (random.nextFloat() - 0.5f) * cellHeight * 0.08f

            // Depth scaling: back rows slightly smaller for perspective
            val depthScale = 0.82f + plant.gridPositionY * 0.09f
            val scaledCellSize = minOf(cellWidth, cellHeight) * depthScale

            val baseX = plant.gridPositionX * cellWidth + (cellWidth - scaledCellSize) / 2
            val baseY = plantAreaTop + plant.gridPositionY * cellHeight

            val offsetX = with(density) { (baseX + jitterX).toDp() }
            val offsetY = with(density) { (baseY + jitterY).toDp() }
            val cellSizeDp = with(density) { scaledCellSize.toDp() }

            PlantComposable(
                plant = plant,
                cellSize = cellSizeDp,
                onClick = { onPlantClick(plant) },
                modifier = Modifier.offset(x = offsetX, y = offsetY),
            )
        }
    }
}

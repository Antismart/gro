package com.example.gro.ui.component.garden

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.example.gro.domain.model.Plant
import kotlin.random.Random

@Composable
fun GardenScene(
    plants: List<Plant>,
    onPlantClick: (Plant) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
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
        val cellSizeDp = with(density) { minOf(cellWidth, cellHeight).toDp() }

        plants.forEach { plant ->
            val random = remember(plant.id) { Random(plant.id) }
            val jitterX = (random.nextFloat() - 0.5f) * cellWidth * 0.15f
            val jitterY = (random.nextFloat() - 0.5f) * cellHeight * 0.1f

            val cellSizePx = with(density) { cellSizeDp.toPx() }
            val baseX = plant.gridPositionX * cellWidth + (cellWidth - cellSizePx) / 2
            val baseY = plantAreaTop + plant.gridPositionY * cellHeight

            val offsetX = with(density) { (baseX + jitterX).toDp() }
            val offsetY = with(density) { (baseY + jitterY).toDp() }

            PlantComposable(
                plant = plant,
                cellSize = cellSizeDp,
                onClick = { onPlantClick(plant) },
                modifier = Modifier.offset(x = offsetX, y = offsetY),
            )
        }
    }
}

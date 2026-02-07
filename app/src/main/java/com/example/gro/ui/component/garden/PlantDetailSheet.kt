package com.example.gro.ui.component.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.Plant
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSand
import com.example.gro.ui.theme.GroSpacing
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailSheet(
    plant: Plant,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GroCream,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
        ) {
            // Species name + plant name
            Text(
                text = plant.species.plantName,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(GroSpacing.xxs))
            Text(
                text = plant.species.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = GroEarth,
            )

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // Growth stage with progress
            DetailRow(label = "Growth stage", value = plant.growthStage.displayName)
            Spacer(modifier = Modifier.height(GroSpacing.xs))
            GrowthProgressBar(currentStage = plant.growthStage)

            Spacer(modifier = Modifier.height(GroSpacing.md))

            // Stats
            DetailRow(label = "Health", value = plant.healthTier.label)
            Spacer(modifier = Modifier.height(GroSpacing.xs))

            val daysSincePlanted = TimeUnit.MILLISECONDS.toDays(
                System.currentTimeMillis() - plant.plantedAt
            ).toInt()
            DetailRow(label = "Planted", value = "$daysSincePlanted days ago")
            Spacer(modifier = Modifier.height(GroSpacing.xs))

            DetailRow(label = "Times watered", value = "${plant.totalDeposits}")
            Spacer(modifier = Modifier.height(GroSpacing.xs))

            DetailRow(label = "Rarity", value = plant.species.rarity.name.lowercase().replaceFirstChar { it.uppercase() })

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // Description
            Text(
                text = plant.species.description,
                style = MaterialTheme.typography.bodyMedium,
                color = GroEarth,
            )

            Spacer(modifier = Modifier.height(GroSpacing.xl))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = GroEarth,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun GrowthProgressBar(currentStage: GrowthStage) {
    val stages = GrowthStage.entries
    val currentIndex = stages.indexOf(currentStage)
    val progress = (currentIndex + 1f) / stages.size

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        stages.forEachIndexed { index, stage ->
            val isReached = index <= currentIndex
            val dotColor = if (isReached) GroGreen else GroSand

            androidx.compose.foundation.Canvas(
                modifier = Modifier.size(8.dp),
            ) {
                drawCircle(color = dotColor)
            }

            if (index < stages.size - 1) {
                Spacer(modifier = Modifier.width(GroSpacing.xxs))
                LinearProgressIndicator(
                    progress = { if (index < currentIndex) 1f else if (index == currentIndex) 0.5f else 0f },
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = GroGreen,
                    trackColor = GroSand,
                )
                Spacer(modifier = Modifier.width(GroSpacing.xxs))
            }
        }
    }
}

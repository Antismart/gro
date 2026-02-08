package com.example.gro.ui.screen.plantdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.domain.model.GrowthStage
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.garden.PlantComposable
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSand
import com.example.gro.ui.theme.GroSpacing
import java.util.concurrent.TimeUnit

@Composable
fun PlantDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    viewModel: PlantDetailViewModel = hiltViewModel(),
) {
    val plant by viewModel.plant.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
    ) {
        plant?.let { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                // Large plant visual at top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PlantComposable(
                        plant = p,
                        cellSize = 180.dp,
                        onClick = {},
                    )
                }

                // Scrollable info card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(GroCream)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.lg),
                ) {
                    Text(
                        text = p.species.plantName,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.xxs))
                    Text(
                        text = p.species.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = GroEarth,
                    )

                    Spacer(modifier = Modifier.height(GroSpacing.lg))

                    // Growth stage progress
                    Text(
                        text = "Growth",
                        style = MaterialTheme.typography.titleSmall,
                        color = GroEarth,
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.xs))
                    GrowthProgressRow(currentStage = p.growthStage)

                    Spacer(modifier = Modifier.height(GroSpacing.lg))

                    // Stats grid
                    DetailRow(label = "Health", value = p.healthTier.label)
                    Spacer(modifier = Modifier.height(GroSpacing.sm))

                    val daysSincePlanted = TimeUnit.MILLISECONDS.toDays(
                        System.currentTimeMillis() - p.plantedAt,
                    ).toInt()
                    DetailRow(label = "Age", value = "$daysSincePlanted days")
                    Spacer(modifier = Modifier.height(GroSpacing.sm))

                    DetailRow(label = "Times watered", value = "${p.totalDeposits}")
                    Spacer(modifier = Modifier.height(GroSpacing.sm))

                    val depositedSol = p.totalDepositedAmount / 1_000_000_000.0
                    DetailRow(label = "Total deposited", value = "${"%.4f".format(depositedSol)} SOL")
                    Spacer(modifier = Modifier.height(GroSpacing.sm))

                    DetailRow(
                        label = "Rarity",
                        value = p.species.rarity.name.lowercase().replaceFirstChar { it.uppercase() },
                    )

                    Spacer(modifier = Modifier.height(GroSpacing.lg))

                    Text(
                        text = p.species.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GroEarth,
                    )

                    Spacer(modifier = Modifier.height(GroSpacing.xl))
                }

                // Bottom action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GroCream)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(GroSpacing.sm),
                ) {
                    GroButton(
                        text = "Water",
                        onClick = onNavigateToDeposit,
                        modifier = Modifier.weight(1f),
                    )
                    GroButton(
                        text = "Back",
                        onClick = onNavigateBack,
                        style = GroButtonStyle.Secondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
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
private fun GrowthProgressRow(currentStage: GrowthStage) {
    val stages = GrowthStage.entries
    val currentIndex = stages.indexOf(currentStage)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        stages.forEachIndexed { index, stage ->
            val isReached = index <= currentIndex
            val dotColor = if (isReached) GroGreen else GroSand

            androidx.compose.foundation.Canvas(modifier = Modifier.size(10.dp)) {
                drawCircle(color = dotColor)
            }

            if (index < stages.size - 1) {
                Spacer(modifier = Modifier.width(GroSpacing.xxs))
                LinearProgressIndicator(
                    progress = { if (index < currentIndex) 1f else if (index == currentIndex) 0.5f else 0f },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = GroGreen,
                    trackColor = GroSand,
                )
                Spacer(modifier = Modifier.width(GroSpacing.xxs))
            }
        }
    }

    Spacer(modifier = Modifier.height(GroSpacing.xxs))

    Text(
        text = currentStage.displayName,
        style = MaterialTheme.typography.labelMedium,
        color = GroGreen,
    )
}

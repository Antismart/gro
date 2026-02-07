package com.example.gro.ui.screen.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.garden.EmptyGardenPrompt
import com.example.gro.ui.component.garden.GardenScene
import com.example.gro.ui.component.garden.PlantDetailSheet
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.JetBrainsMonoFamily

@Composable
fun GardenScreen(
    onDisconnect: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val disconnected by viewModel.disconnected.collectAsState()

    LaunchedEffect(disconnected) {
        if (disconnected) onDisconnect()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
    ) {
        // TOP — Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(GroSpacing.xl))
            Text(
                text = uiState.greeting,
                style = MaterialTheme.typography.headlineMedium,
                color = GroEarth,
            )
            Spacer(modifier = Modifier.height(GroSpacing.xxs))
            uiState.walletAddress?.let { address ->
                Text(
                    text = "${address.take(4)}...${address.takeLast(4)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GroEarth,
                )
            }
            Spacer(modifier = Modifier.height(GroSpacing.sm))
            Text(
                text = "${"%.4f".format(uiState.totalPortfolioValue)} SOL",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = JetBrainsMonoFamily,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // MIDDLE — Garden Scene
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp),
                        color = GroGreen,
                        strokeWidth = 3.dp,
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(GroSpacing.md))
                        GroButton(
                            text = "Try again",
                            onClick = { viewModel.loadGarden() },
                            style = GroButtonStyle.Secondary,
                        )
                    }
                }
                uiState.plants.isEmpty() -> {
                    EmptyGardenPrompt(
                        onDeposit = onNavigateToDeposit,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                else -> {
                    GardenScene(
                        plants = uiState.plants,
                        onPlantClick = { viewModel.selectPlant(it) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        // BOTTOM — Quick Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(GroSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GroButton(
                text = "Water",
                onClick = onNavigateToDeposit,
                modifier = Modifier.weight(1f),
            )
            GroButton(
                text = "Disconnect",
                onClick = { viewModel.disconnect() },
                style = GroButtonStyle.Tertiary,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(GroSpacing.sm))
    }

    // Plant detail bottom sheet
    uiState.selectedPlant?.let { plant ->
        PlantDetailSheet(
            plant = plant,
            onDismiss = { viewModel.dismissPlantDetail() },
        )
    }
}

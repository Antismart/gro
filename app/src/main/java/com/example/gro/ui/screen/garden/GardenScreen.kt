package com.example.gro.ui.screen.garden

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.domain.model.GardenWeather
import com.example.gro.domain.model.GrowthStage
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.model.Streak
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.garden.EmptyGardenPrompt
import com.example.gro.ui.component.garden.GardenScene
import com.example.gro.ui.component.garden.StreakBadge
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.JetBrainsMonoFamily

@Composable
fun GardenScreen(
    onDisconnect: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    onNavigateToPlantDetail: (Long) -> Unit = {},
    onNavigateToVisit: () -> Unit = {},
    onNavigateToJournal: () -> Unit = {},
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val disconnected by viewModel.disconnected.collectAsState()

    LaunchedEffect(disconnected) {
        if (disconnected) onDisconnect()
    }

    GardenContent(
        uiState = uiState,
        onNavigateToDeposit = onNavigateToDeposit,
        onNavigateToPlantDetail = onNavigateToPlantDetail,
        onNavigateToVisit = onNavigateToVisit,
        onNavigateToJournal = onNavigateToJournal,
        onDisconnect = { viewModel.disconnect() },
        onRetry = { viewModel.loadGarden() },
    )
}

@Composable
private fun GardenContent(
    uiState: GardenUiState,
    onNavigateToDeposit: () -> Unit,
    onNavigateToPlantDetail: (Long) -> Unit,
    onNavigateToVisit: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onDisconnect: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP — Header with gradient fade into scene
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(GroCream, Color(0xFFF0EBE1)),
                        ),
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = GroSpacing.lg)
                    .padding(top = GroSpacing.md, bottom = GroSpacing.lg),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = uiState.greeting,
                            style = MaterialTheme.typography.titleMedium,
                            color = GroEarth,
                        )
                        uiState.streak?.let { streak ->
                            if (streak.currentStreak > 0) {
                                Spacer(modifier = Modifier.width(GroSpacing.sm))
                                StreakBadge(streakCount = streak.currentStreak)
                            }
                        }
                    }
                    uiState.walletAddress?.let { address ->
                        Spacer(modifier = Modifier.height(GroSpacing.xxs))
                        Text(
                            text = "${address.take(4)}...${address.takeLast(4)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GroEarth.copy(alpha = 0.6f),
                        )
                    }
                    Spacer(modifier = Modifier.height(GroSpacing.sm))
                    Text(
                        text = "${"%.4f".format(uiState.totalPortfolioValue)} SOL",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = JetBrainsMonoFamily,
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                }
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
                                onClick = onRetry,
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
                            onPlantClick = { onNavigateToPlantDetail(it.id) },
                            weather = uiState.weather,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            // BOTTOM — Action Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        clip = false,
                    )
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color(0xFFFAF7F2)) // GroSurface
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(GroSpacing.xs),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(GroSpacing.sm),
                    ) {
                        GroButton(
                            text = "Water",
                            onClick = onNavigateToDeposit,
                            modifier = Modifier.weight(1f),
                        )
                        GroButton(
                            text = "Visit",
                            onClick = onNavigateToVisit,
                            style = GroButtonStyle.Secondary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(GroSpacing.sm),
                    ) {
                        GroButton(
                            text = "Journal",
                            onClick = onNavigateToJournal,
                            style = GroButtonStyle.Secondary,
                            modifier = Modifier.weight(1f),
                        )
                        GroButton(
                            text = "Disconnect",
                            onClick = onDisconnect,
                            style = GroButtonStyle.Tertiary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewGardenWithPlants() {
    val now = System.currentTimeMillis()
    val fakePlants = listOf(
        Plant(id = 1, walletAddress = "abc", tokenMint = PlantSpecies.SOL.tokenMint,
            species = PlantSpecies.SOL, growthStage = GrowthStage.MATURE, healthScore = 90,
            growthPoints = 80f, plantedAt = now - 86400000 * 14, lastWateredAt = now,
            totalDeposits = 8, totalDepositedAmount = 500_000_000, gridPositionX = 1, gridPositionY = 1),
        Plant(id = 2, walletAddress = "abc", tokenMint = PlantSpecies.BONK.tokenMint,
            species = PlantSpecies.BONK, growthStage = GrowthStage.SPROUT, healthScore = 75,
            growthPoints = 20f, plantedAt = now - 86400000 * 3, lastWateredAt = now,
            totalDeposits = 3, totalDepositedAmount = 100_000_000, gridPositionX = 2, gridPositionY = 0),
    )
    GardenContent(
        uiState = GardenUiState(
            walletAddress = "AbCdEfGhIjKlMnOpQrStUvWx",
            plants = fakePlants,
            totalPortfolioValue = 1.2345,
            isLoading = false,
            greeting = "Good afternoon",
            streak = Streak("abc", 5, 12, "2026-02-07", 42),
            weather = GardenWeather.SUNNY,
        ),
        onNavigateToDeposit = {},
        onNavigateToPlantDetail = {},
        onNavigateToVisit = {},
        onNavigateToJournal = {},
        onDisconnect = {},
        onRetry = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewGardenEmpty() {
    GardenContent(
        uiState = GardenUiState(
            walletAddress = "AbCdEfGhIjKlMnOpQrStUvWx",
            plants = emptyList(),
            totalPortfolioValue = 0.0,
            isLoading = false,
            greeting = "Good morning",
        ),
        onNavigateToDeposit = {},
        onNavigateToPlantDetail = {},
        onNavigateToVisit = {},
        onNavigateToJournal = {},
        onDisconnect = {},
        onRetry = {},
    )
}

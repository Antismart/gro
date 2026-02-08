package com.example.gro.ui.screen.deposit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.garden.SeedPlantingAnimation
import com.example.gro.ui.theme.GroBark
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSand
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.JetBrainsMonoFamily
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

@Composable
fun DepositScreen(
    activityResultSender: ActivityResultSender,
    onNavigateBack: () -> Unit,
    viewModel: DepositViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.depositSuccess, uiState.showAnimation) {
        if (uiState.depositSuccess && !uiState.showAnimation) {
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(GroSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(GroSpacing.xl))

            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                GroButton(
                    text = "Back",
                    onClick = onNavigateBack,
                    style = GroButtonStyle.Tertiary,
                )
            }

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // Title
            Text(
                text = "Water your garden",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(GroSpacing.md))

            // Species picker
            Text(
                text = "Choose your plant",
                style = MaterialTheme.typography.labelLarge,
                color = GroEarth,
            )
            Spacer(modifier = Modifier.height(GroSpacing.sm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(GroSpacing.xs),
            ) {
                viewModel.availableSpecies.forEach { species ->
                    SpeciesChip(
                        species = species,
                        selected = uiState.selectedSpecies == species,
                        onClick = { viewModel.selectSpecies(species) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // Amount display
            Text(
                text = "${"%.4f".format(uiState.amountSol)} SOL",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = JetBrainsMonoFamily,
                ),
                color = if (uiState.amountLamports > 0) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    GroEarth
                },
            )
            Text(
                text = "to grow ${uiState.selectedSpecies.plantName}",
                style = MaterialTheme.typography.bodyMedium,
                color = GroEarth,
            )

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // Preset chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(GroSpacing.sm),
            ) {
                listOf(0.01, 0.05, 0.1).forEach { amount ->
                    PresetChip(
                        label = "$amount SOL",
                        selected = uiState.amountSol == amount && uiState.customAmountText.isEmpty(),
                        onClick = { viewModel.selectPresetAmount(amount) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // Custom amount
            OutlinedTextField(
                value = uiState.customAmountText,
                onValueChange = { viewModel.setCustomAmount(it) },
                label = { Text("Custom amount (SOL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GroGreen,
                    unfocusedBorderColor = GroSand,
                    cursorColor = GroGreen,
                ),
            )

            Spacer(modifier = Modifier.weight(1f))

            // Error
            uiState.error?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(GroSpacing.sm))
            }

            // Submit
            GroButton(
                text = if (uiState.amountLamports > 0) "Plant ${uiState.selectedSpecies.plantName}" else "Enter an amount",
                onClick = { viewModel.submitDeposit(activityResultSender) },
                enabled = uiState.amountLamports > 0 && !uiState.isSubmitting,
                isLoading = uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(GroSpacing.lg))
        }

        // Success animation overlay
        if (uiState.showAnimation) {
            SeedPlantingAnimation(
                onAnimationComplete = { viewModel.dismissAnimation() },
            )
        }
    }
}

@Composable
private fun SpeciesChip(
    species: PlantSpecies,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (selected) GroGreen else GroCream
    val textColor = if (selected) GroCream else GroBark
    val borderColor = if (selected) GroGreen else GroSand

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = GroSpacing.md, vertical = GroSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = species.displayName,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
        )
        Text(
            text = species.plantName,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) GroCream.copy(alpha = 0.8f) else GroEarth,
        )
    }
}

@Composable
private fun PresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (selected) GroGreen else GroCream
    val textColor = if (selected) GroCream else GroEarth
    val borderColor = if (selected) GroGreen else GroSand

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = GroSpacing.md, vertical = GroSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
        )
    }
}

package com.example.gro.ui.screen.visit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.garden.GardenScene
import com.example.gro.ui.theme.GroBark
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSand
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.GroSunlight
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

@Composable
fun VisitGardenScreen(
    onNavigateBack: () -> Unit,
    activityResultSender: ActivityResultSender,
    viewModel: VisitGardenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    VisitContent(
        uiState = uiState,
        onUpdateAddress = { viewModel.updateAddress(it) },
        onVisit = { viewModel.visitGarden() },
        onSendSunflower = { viewModel.sendSunflower(activityResultSender) },
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun VisitContent(
    uiState: VisitUiState,
    onUpdateAddress: (String) -> Unit,
    onVisit: () -> Unit,
    onSendSunflower: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GroSpacing.lg)
                    .padding(top = GroSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Visit a Garden",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(GroSpacing.xs))
                Text(
                    text = "Enter a Solana address to peek at their garden",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GroEarth,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(GroSpacing.md))

            // Address input
            if (!uiState.hasVisited || uiState.plants.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GroSpacing.lg),
                ) {
                    OutlinedTextField(
                        value = uiState.friendAddress,
                        onValueChange = onUpdateAddress,
                        label = { Text("Wallet address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GroGreen,
                            unfocusedBorderColor = GroSand,
                            focusedLabelColor = GroGreen,
                            unfocusedLabelColor = GroEarth,
                            cursorColor = GroBark,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = { onVisit() }),
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.md))
                    GroButton(
                        text = if (uiState.isLoading) "Looking..." else "Visit",
                        onClick = onVisit,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    uiState.error?.let { error ->
                        Spacer(modifier = Modifier.height(GroSpacing.sm))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            } else {
                // Friend's address header
                Text(
                    text = "${uiState.friendAddress.take(4)}...${uiState.friendAddress.takeLast(4)}'s garden",
                    style = MaterialTheme.typography.titleMedium,
                    color = GroEarth,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GroSpacing.lg),
                )
            }

            // Garden scene or loading
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
                    uiState.hasVisited && uiState.plants.isNotEmpty() -> {
                        GardenScene(
                            plants = uiState.plants,
                            onPlantClick = {},
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            // Bottom actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
            ) {
                if (uiState.hasVisited && uiState.plants.isNotEmpty()) {
                    GroButton(
                        text = if (uiState.sunflowerSent) "Sunflower sent!" else "Leave a sunflower",
                        onClick = onSendSunflower,
                        enabled = !uiState.isSendingSunflower && !uiState.sunflowerSent,
                        isLoading = uiState.isSendingSunflower,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (uiState.sunflowerSent) {
                        Spacer(modifier = Modifier.height(GroSpacing.xxs))
                        Text(
                            text = "Your sunflower has been planted in their garden",
                            style = MaterialTheme.typography.bodySmall,
                            color = GroSunlight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    uiState.error?.let { error ->
                        Spacer(modifier = Modifier.height(GroSpacing.xs))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.height(GroSpacing.sm))
                }
                GroButton(
                    text = "Back to my garden",
                    onClick = onNavigateBack,
                    style = GroButtonStyle.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewVisitInput() {
    VisitContent(
        uiState = VisitUiState(),
        onUpdateAddress = {},
        onVisit = {},
        onSendSunflower = {},
        onNavigateBack = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewVisitWithAddress() {
    VisitContent(
        uiState = VisitUiState(
            friendAddress = "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU",
        ),
        onUpdateAddress = {},
        onVisit = {},
        onSendSunflower = {},
        onNavigateBack = {},
    )
}

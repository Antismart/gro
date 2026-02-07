package com.example.gro.ui.screen.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.GroCard
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.JetBrainsMonoFamily

@Composable
fun GardenScreen(
    onDisconnect: () -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val disconnected by viewModel.disconnected.collectAsState()

    LaunchedEffect(disconnected) {
        if (disconnected) {
            onDisconnect()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream)
            .padding(GroSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(GroSpacing.xxl))

        // Greeting
        Text(
            text = "Welcome to your garden",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(GroSpacing.xs))

        // Wallet address
        uiState.walletAddress?.let { address ->
            Text(
                text = "${address.take(6)}...${address.takeLast(4)}",
                style = MaterialTheme.typography.bodyMedium,
                color = GroEarth,
            )
        }

        Spacer(modifier = Modifier.height(GroSpacing.xl))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = GroGreen,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp,
                )
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.md))
                    GroButton(
                        text = "Try again",
                        onClick = { viewModel.loadGarden() },
                        style = GroButtonStyle.Secondary,
                    )
                }
            }
        } else {
            // SOL Balance
            GroCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SOL Balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = GroEarth,
                )
                Spacer(modifier = Modifier.height(GroSpacing.xs))
                Text(
                    text = "%.4f SOL".format(uiState.solBalance / 1_000_000_000.0),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = JetBrainsMonoFamily,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(GroSpacing.md))

            // Token accounts
            if (uiState.tokenAccounts.isNotEmpty()) {
                GroCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Token Holdings",
                        style = MaterialTheme.typography.labelLarge,
                        color = GroEarth,
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.sm))
                    uiState.tokenAccounts.forEach { token ->
                        val displayAmount = token.amount / Math.pow(10.0, token.decimals.toDouble())
                        Text(
                            text = "${token.mint.take(8)}... — ${"%.4f".format(displayAmount)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = JetBrainsMonoFamily,
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(GroSpacing.xxs))
                    }
                }
            } else {
                GroCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Your garden is empty",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.xs))
                    Text(
                        text = "Plant your first seed by depositing SOL",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GroEarth,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Disconnect button (for testing)
            GroButton(
                text = "Disconnect wallet",
                onClick = { viewModel.disconnect() },
                style = GroButtonStyle.Tertiary,
            )

            Spacer(modifier = Modifier.height(GroSpacing.lg))
        }
    }
}

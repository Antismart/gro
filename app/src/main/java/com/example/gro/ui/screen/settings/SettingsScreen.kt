package com.example.gro.ui.screen.settings

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.theme.GroBark
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroDivider
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSand
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.GroSurface

@Composable
fun SettingsScreen(
    onDisconnect: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onToggleNotifications = { viewModel.toggleNotifications() },
        onDisconnect = {
            viewModel.disconnect()
            onDisconnect()
        },
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onToggleNotifications: () -> Unit,
    onDisconnect: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
            textAlign = TextAlign.Center,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GroSpacing.lg),
        ) {
            // Wallet section
            SectionHeader(title = "Wallet")
            SettingsCard {
                Column(modifier = Modifier.padding(GroSpacing.md)) {
                    Text(
                        text = "Connected address",
                        style = MaterialTheme.typography.bodySmall,
                        color = GroEarth,
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.xxs))
                    Text(
                        text = uiState.walletAddress ?: "Not connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GroBark,
                    )
                    Spacer(modifier = Modifier.height(GroSpacing.md))
                    GroButton(
                        text = "Disconnect wallet",
                        onClick = onDisconnect,
                        style = GroButtonStyle.Secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // Notifications section
            SectionHeader(title = "Notifications")
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GroSpacing.md, vertical = GroSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily reminders",
                            style = MaterialTheme.typography.bodyLarge,
                            color = GroBark,
                        )
                        Text(
                            text = "Growth updates and streak alerts",
                            style = MaterialTheme.typography.bodySmall,
                            color = GroEarth,
                        )
                    }
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { onToggleNotifications() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GroGreen,
                            checkedTrackColor = GroGreen.copy(alpha = 0.3f),
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(GroSpacing.lg))

            // About section
            SectionHeader(title = "About")
            SettingsCard {
                Column(modifier = Modifier.padding(GroSpacing.md)) {
                    AboutRow("App", "Gr\u014D")
                    HorizontalDivider(color = GroDivider)
                    AboutRow("Version", "1.0.0")
                    HorizontalDivider(color = GroDivider)
                    AboutRow("Network", uiState.cluster)
                    HorizontalDivider(color = GroDivider)
                    AboutRow("Built for", "Solana Mobile Hackathon")
                }
            }

            Spacer(modifier = Modifier.height(GroSpacing.xxl))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = GroEarth,
        modifier = Modifier.padding(bottom = GroSpacing.xs),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GroSurface),
    ) {
        content()
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = GroSpacing.sm),
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
            color = GroBark,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewSettings() {
    SettingsContent(
        uiState = SettingsUiState(
            walletAddress = "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU",
            notificationsEnabled = true,
            cluster = "devnet",
        ),
        onToggleNotifications = {},
        onDisconnect = {},
    )
}

package com.example.gro.ui.screen.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.JournalEntry
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.journal.JournalEntryCard
import com.example.gro.ui.component.journal.WeeklySummary
import com.example.gro.ui.component.journal.WeeklySummaryCard
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroSpacing

@Composable
fun JournalScreen(
    onNavigateBack: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    JournalContent(uiState = uiState, onNavigateBack = onNavigateBack)
}

@Composable
private fun JournalContent(
    uiState: JournalUiState,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        // Header
        Text(
            text = "Garden Journal",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
            textAlign = TextAlign.Center,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = GroSpacing.lg),
        ) {
            // Weekly summary at top
            item {
                WeeklySummaryCard(summary = uiState.weeklySummary)
                Spacer(modifier = Modifier.height(GroSpacing.lg))
            }

            if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else if (uiState.entries.isEmpty() && !uiState.isLoading) {
                item {
                    Text(
                        text = "No journal entries yet. Water your garden to get started!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GroEarth,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            items(uiState.entries, key = { it.id }) { entry ->
                JournalEntryCard(
                    entry = entry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = GroSpacing.xs),
                )
            }
        }

        // Back button
        GroButton(
            text = "Back to garden",
            onClick = onNavigateBack,
            style = GroButtonStyle.Secondary,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.md),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewJournalWithEntries() {
    val now = System.currentTimeMillis()
    JournalContent(
        uiState = JournalUiState(
            entries = listOf(
                JournalEntry(1, "abc", now - 3600000, JournalAction.DEPOSIT,
                    "Deposited 0.0500 SOL to grow Sol Sprout", 2),
                JournalEntry(2, "abc", now - 7200000, JournalAction.GROWTH,
                    "Sol Sprout advanced to Sprout stage", 2),
                JournalEntry(3, "abc", now - 86400000, JournalAction.STREAK,
                    "Streak reached 5 days!", 2),
                JournalEntry(4, "abc", now - 86400000 * 2, JournalAction.DEPOSIT,
                    "Deposited 0.1000 SOL to grow Bonk Bloom", 1),
            ),
            weeklySummary = WeeklySummary(deposits = 3, growthEvents = 2, streakDays = 5, blooms = 0),
            isLoading = false,
        ),
        onNavigateBack = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewJournalEmpty() {
    JournalContent(
        uiState = JournalUiState(isLoading = false),
        onNavigateBack = {},
    )
}

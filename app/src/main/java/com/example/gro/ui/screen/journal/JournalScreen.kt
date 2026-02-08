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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.component.journal.JournalEntryCard
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

            if (uiState.entries.isEmpty() && !uiState.isLoading) {
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

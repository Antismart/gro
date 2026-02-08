package com.example.gro.ui.screen.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.JournalEntry
import com.example.gro.domain.repository.JournalRepository
import com.example.gro.domain.repository.WalletRepository
import com.example.gro.ui.component.journal.WeeklySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val entries: List<JournalEntry> = emptyList(),
    val weeklySummary: WeeklySummary = WeeklySummary(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val walletRepository: WalletRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState

    init {
        loadJournal()
    }

    private fun loadJournal() {
        val address = walletRepository.getConnectedAddress() ?: return

        viewModelScope.launch {
            journalRepository.observeEntries(address).collect { entries ->
                val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
                val weekEntries = entries.filter { it.timestamp >= oneWeekAgo }

                val summary = WeeklySummary(
                    deposits = weekEntries.count { it.action == JournalAction.DEPOSIT },
                    growthEvents = weekEntries.count { it.action == JournalAction.GROWTH },
                    streakDays = weekEntries.count { it.action == JournalAction.STREAK },
                    blooms = weekEntries.count { it.action == JournalAction.BLOOM },
                )

                _uiState.update {
                    it.copy(
                        entries = entries,
                        weeklySummary = summary,
                        isLoading = false,
                    )
                }
            }
        }
    }
}

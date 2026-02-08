package com.example.gro.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.data.local.datastore.UserPreferences
import com.example.gro.data.remote.SolanaConfig
import com.example.gro.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val walletAddress: String? = null,
    val notificationsEnabled: Boolean = true,
    val cluster: String = "devnet",
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val userPreferences: UserPreferences,
    private val solanaConfig: SolanaConfig,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            val prefs = userPreferences.userPreferencesFlow.first()
            _uiState.update {
                it.copy(
                    walletAddress = walletRepository.getConnectedAddress(),
                    notificationsEnabled = prefs.notificationsEnabled,
                    cluster = solanaConfig.cluster,
                )
            }
        }
    }

    fun toggleNotifications() {
        val newValue = !_uiState.value.notificationsEnabled
        _uiState.update { it.copy(notificationsEnabled = newValue) }
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(newValue)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            walletRepository.disconnect()
        }
    }
}

package com.example.homigo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homigo.domain.LogoutResult
import com.example.homigo.domain.LogoutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LogoutUiState {
    object Idle : LogoutUiState()
    object Loading : LogoutUiState()
    data class Success(val message: String) : LogoutUiState()
    data class Error(val message: String) : LogoutUiState()
}

sealed class LogoutNavigationEvent {
    object NavigateToWelcome : LogoutNavigationEvent()
}

class LogoutViewModel(
    private val logoutUseCase: LogoutUseCase = LogoutUseCase()
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutUiState>(LogoutUiState.Idle)
    val logoutState: StateFlow<LogoutUiState> = _logoutState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LogoutNavigationEvent>()
    val navigationEvent: SharedFlow<LogoutNavigationEvent> = _navigationEvent.asSharedFlow()

    fun logout() {
        _logoutState.value = LogoutUiState.Loading
        viewModelScope.launch {
            when (val result = logoutUseCase.execute()) {
                is LogoutResult.Success -> {
                    _logoutState.value = LogoutUiState.Success(result.message)
                    _navigationEvent.emit(LogoutNavigationEvent.NavigateToWelcome)
                }
                is LogoutResult.NoInternet -> {
                    _logoutState.value = LogoutUiState.Error(result.message)
                }
                is LogoutResult.Failure -> {
                    _logoutState.value = LogoutUiState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _logoutState.value = LogoutUiState.Idle
    }
}

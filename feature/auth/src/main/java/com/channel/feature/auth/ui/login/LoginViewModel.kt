package com.channel.feature.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.feature.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(username = value, submitError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, submitError = null) }
    }

    fun submit() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            when (val result = authRepository.login(state.username, state.password)) {
                // Success flips AuthStateManager, which drives the app root past this screen.
                is ApiResult.Success -> Unit
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
            }
        }
    }
}

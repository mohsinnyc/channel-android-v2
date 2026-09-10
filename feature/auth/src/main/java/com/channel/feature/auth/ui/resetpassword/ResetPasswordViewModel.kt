package com.channel.feature.auth.ui.resetpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.feature.auth.data.AuthRepository
import com.channel.feature.auth.navigation.ResetPasswordDestination
import com.channel.feature.auth.validation.PasswordValidator
import com.channel.feature.auth.validation.ValidationResult
import com.channel.feature.auth.validation.VerificationCodeValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val email = savedStateHandle.toRoute<ResetPasswordDestination>().email

    private val _uiState = MutableStateFlow(ResetPasswordUiState(email = email))
    val uiState: StateFlow<ResetPasswordUiState> = _uiState

    private val _events = Channel<ResetPasswordEvent>(Channel.BUFFERED)
    val events: Flow<ResetPasswordEvent> = _events.receiveAsFlow()

    fun onCodeChanged(value: String) {
        _uiState.update { it.copy(code = value, codeError = null) }
    }

    fun onNewPasswordChanged(value: String) {
        _uiState.update { it.copy(newPassword = value, passwordError = null) }
    }

    fun submit() {
        val state = _uiState.value
        val codeResult = VerificationCodeValidator.validate(state.code)
        val passwordResult = PasswordValidator.validate(state.newPassword)

        val codeError = (codeResult as? ValidationResult.Invalid)?.reason
        val passwordError = (passwordResult as? ValidationResult.Invalid)?.reason

        if (codeError != null || passwordError != null) {
            _uiState.update { it.copy(codeError = codeError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            when (val result = authRepository.resetPassword(email, state.code, state.newPassword)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(ResetPasswordEvent.PasswordReset)
                }
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
            }
        }
    }
}

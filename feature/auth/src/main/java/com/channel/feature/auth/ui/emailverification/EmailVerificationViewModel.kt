package com.channel.feature.auth.ui.emailverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.core.network.session.AuthStateManager
import com.channel.feature.auth.data.AuthRepository
import com.channel.feature.auth.validation.ValidationResult
import com.channel.feature.auth.validation.VerificationCodeValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStateManager: AuthStateManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState

    fun onCodeChanged(value: String) {
        _uiState.update { it.copy(code = value, codeError = null) }
    }

    fun resendCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true, resendError = null, resendConfirmationVisible = false) }
            when (val result = authRepository.requestEmailVerification()) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isResending = false, resendConfirmationVisible = true) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isResending = false, resendError = result.toNetworkError()) }
                is ApiResult.Exception ->
                    _uiState.update { it.copy(isResending = false, resendError = result.toNetworkError()) }
            }
        }
    }

    fun verify() {
        val state = _uiState.value
        val codeResult = VerificationCodeValidator.validate(state.code)
        val codeError = (codeResult as? ValidationResult.Invalid)?.reason
        if (codeError != null) {
            _uiState.update { it.copy(codeError = codeError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, verifyError = null) }
            when (val result = authRepository.verifyEmail(state.code)) {
                // Success re-triggers the app root's status check, which moves past this screen.
                is ApiResult.Success -> authStateManager.requestStatusRefresh()
                is ApiResult.Error -> _uiState.update { it.copy(isVerifying = false, verifyError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isVerifying = false, verifyError = result.toNetworkError()) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authStateManager.logout()
        }
    }
}

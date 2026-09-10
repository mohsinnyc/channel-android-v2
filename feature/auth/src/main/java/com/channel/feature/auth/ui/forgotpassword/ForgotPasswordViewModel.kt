package com.channel.feature.auth.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.feature.auth.data.AuthRepository
import com.channel.feature.auth.validation.EmailValidator
import com.channel.feature.auth.validation.ValidationResult
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
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    private val _events = Channel<ForgotPasswordEvent>(Channel.BUFFERED)
    val events: Flow<ForgotPasswordEvent> = _events.receiveAsFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun submit() {
        val state = _uiState.value
        val emailResult = EmailValidator.validate(state.email)
        val emailError = (emailResult as? ValidationResult.Invalid)?.reason
        if (emailError != null) {
            _uiState.update { it.copy(emailError = emailError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            when (val result = authRepository.forgotPassword(state.email)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(ForgotPasswordEvent.CodeSent(state.email))
                }
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
            }
        }
    }
}

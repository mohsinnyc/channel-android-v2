package com.channel.feature.auth.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.feature.auth.data.AuthRepository
import com.channel.feature.auth.validation.EmailValidator
import com.channel.feature.auth.validation.PasswordValidator
import com.channel.feature.auth.validation.UsernameValidator
import com.channel.feature.auth.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(username = value, usernameError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun submit() {
        val state = _uiState.value
        val usernameResult = UsernameValidator.validate(state.username)
        val passwordResult = PasswordValidator.validate(state.password)
        val emailResult = EmailValidator.validate(state.email)

        val usernameError = (usernameResult as? ValidationResult.Invalid)?.reason
        val passwordError = (passwordResult as? ValidationResult.Invalid)?.reason
        val emailError = (emailResult as? ValidationResult.Invalid)?.reason

        if (usernameError != null || passwordError != null || emailError != null) {
            _uiState.update {
                it.copy(usernameError = usernameError, passwordError = passwordError, emailError = emailError)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            when (val result = authRepository.signUp(state.username, state.password, state.email)) {
                // Success flips AuthStateManager, which drives the app root past this screen.
                is ApiResult.Success -> Unit
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
            }
        }
    }
}

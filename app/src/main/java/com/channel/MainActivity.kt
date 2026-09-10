package com.channel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.channel.core.designsystem.theme.ChannelTheme
import com.channel.navigation.AppDestination
import com.channel.navigation.RootViewModel
import com.channel.feature.auth.ui.emailverification.EmailVerificationDestination
import com.channel.ui.AuthDestinationHost
import com.channel.ui.BannedPlaceholderDestination
import com.channel.ui.ErrorDestination
import com.channel.ui.LoadingDestination
import com.channel.ui.MainPlaceholderDestination
import com.channel.ui.OnboardingDestinationHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Holds the OS splash screen only for the initial "do we have a token"
     * resolution. Once the destination leaves Loading for the first time, this
     * flips false permanently — the splash never reappears for a later Loading
     * state (e.g. the /auth/status call after that), which renders its own
     * in-app LoadingDestination instead.
     */
    @Volatile private var keepOsSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepOsSplashScreen }

        setContent {
            val viewModel: RootViewModel = hiltViewModel()
            val destination by viewModel.destination.collectAsState()

            LaunchedEffect(destination) {
                if (destination != AppDestination.Loading) keepOsSplashScreen = false
            }

            ChannelTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val current = destination) {
                        AppDestination.Loading -> LoadingDestination()
                        AppDestination.Auth -> AuthDestinationHost()
                        AppDestination.Banned -> BannedPlaceholderDestination()
                        AppDestination.EmailVerification -> EmailVerificationDestination()
                        is AppDestination.Onboarding -> OnboardingDestinationHost(current.state)
                        AppDestination.Main -> MainPlaceholderDestination()
                        is AppDestination.Error -> ErrorDestination(current.reason, onRetry = viewModel::retry)
                    }
                }
            }
        }
    }
}

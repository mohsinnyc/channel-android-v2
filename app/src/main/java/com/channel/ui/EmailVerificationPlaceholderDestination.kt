package com.channel.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.channel.R

@Composable
fun EmailVerificationPlaceholderDestination() =
    PlaceholderContent(stringResource(R.string.destination_placeholder_email_verification))

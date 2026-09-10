package com.channel.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

/**
 * Material3's own baseline palette — deliberately not a custom brand color yet.
 * Swap these two for real brand colors in one place once they exist; every
 * screen already reads through MaterialTheme.colorScheme, never a literal color.
 */
internal val ChannelLightColorScheme: ColorScheme = lightColorScheme()
internal val ChannelDarkColorScheme: ColorScheme = darkColorScheme()

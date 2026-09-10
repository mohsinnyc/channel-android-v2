package com.channel.core.media

data class ImagePickerLaunchers(
    val launchCamera: () -> Unit,
    val launchGallery: () -> Unit,
)

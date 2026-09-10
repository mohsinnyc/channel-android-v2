package com.channel.core.network.model.post

import kotlinx.serialization.Serializable

@Serializable
data class PostCategoryData(
    val id: String,
    val label: String,
    val sortOrder: Int? = null,
    val isActive: Boolean = true,
)

package com.channel.core.network.model.post

import kotlinx.serialization.Serializable

@Serializable
enum class PostStatus { AVAILABLE, MODERATED, DELETED }

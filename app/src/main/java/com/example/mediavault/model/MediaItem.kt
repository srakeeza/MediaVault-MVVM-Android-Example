package com.example.mediavault.model

import android.net.Uri

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val name: String,
    val date: String,
    val size: Long,
    val mimeType: String
)
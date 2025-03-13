package com.mani.wirup

data class UploadResponse(
    val summary: String,
    val suggestions: List<SuggestedTask>
)

data class SuggestedTask(
    val title: String,
    val description: String
)
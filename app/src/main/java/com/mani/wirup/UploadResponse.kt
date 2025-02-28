package com.mani.wirup

data class UploadResponse(
    val summary: String,
    val suggestions: List<SuggestedTask> // List of suggested tasks
)

data class SuggestedTask(
    val title: String,       // Title of the suggested task
    val description: String // Description of the suggested task
)
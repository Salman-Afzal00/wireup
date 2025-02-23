package com.mani.wirup

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,
    val time: String,
    val priority: String,
    val isCompleted: Boolean = false,
    val isPending: Boolean = false,
    val clientId: Int? = null, // Added clientId
    val duration: Long = 0 // Added duration in minutes
) : Parcelable
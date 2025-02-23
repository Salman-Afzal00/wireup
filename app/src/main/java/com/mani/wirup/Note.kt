package com.mani.wirup

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: Long = 1, // Fixed ID for the single note
    val title: String,
    val content: String
) : Parcelable
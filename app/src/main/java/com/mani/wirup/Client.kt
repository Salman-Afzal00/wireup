package com.mani.wirup

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "clients")
data class Client(
    @PrimaryKey
    val id: Int,
    val name: String,
    val contact: String,
    val alternativeContact: String
) : Parcelable
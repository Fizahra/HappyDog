package com.example.happydog.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    val userid: String? =  null,
    val username: String? = null,
    val useremail: String? = null,
    val status: String? = null,
    val imageUrl: String? = null,
    val usernomor: String? = null,
    val role: String? = null,
): Parcelable
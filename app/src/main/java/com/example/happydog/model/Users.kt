package com.example.happydog.model

import android.os.Parcel
import android.os.Parcelable

data class Users(
    val userid: String? =  "",
    val username: String? = "",
    val useremail: String? = "",
    val status: String? = "",
    val imageUrl: String? = "",
    val usernomor: String? = "",
    val role: String? = "",
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(username)
        parcel.writeString(useremail)
        parcel.writeString(status)
        parcel.writeString(imageUrl)
        parcel.writeString(usernomor)
        parcel.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }

}
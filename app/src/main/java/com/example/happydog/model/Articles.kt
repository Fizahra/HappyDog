package com.example.happydog.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Articles(
    val article : String? = null,
    val title: String? = null,
    val author: String? = null,
    val category: String? = null,
    val date: String? = null,
    val imageArticle: String? = null
): Parcelable
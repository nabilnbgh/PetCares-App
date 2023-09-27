package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val nama_reviewer: String,
    val foto_reviewer : String,
    val review_text  : String?,
    val rating : Double
) : Parcelable

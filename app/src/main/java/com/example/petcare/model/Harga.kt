package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Harga(
    val harga : String,
    val deskripsi : String,
) : Parcelable

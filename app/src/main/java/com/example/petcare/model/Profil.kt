package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profil(
    val _id : String,
    val name : String,
    val email : String,
    val password    : String,
    val foto_profil : String,
    val notelp    : String?
) : Parcelable

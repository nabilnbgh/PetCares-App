package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EditProfil(
    val email : String,
    val name : String,
    val notelp : String,
) : Parcelable

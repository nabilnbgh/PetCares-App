package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val foto_berita : Int,
    val judul_berita : String,
    val konten_berita : String
)  :Parcelable
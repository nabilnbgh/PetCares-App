package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VolunteerLocation(
    val nama_tempat_voluntir : String,
    val alamat          : String,
    val rating          : Double,
    val latitude        : Double,
    val longitude       : Double,
    val jenis_voluntir  : Int,
    var distance        : Double?,
    var id_foto         : Int
) : Parcelable

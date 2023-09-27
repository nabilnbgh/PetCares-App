package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VaccineLocation(
    val nama_lokasi             : String,
    val alamat                  : String,
    val rating                  : Double,
    val latitude                : Double,
    val longitude               : Double,
    val foto_lokasi             : String,
    val jam_operasional         : String,
    val username                : String,
    val notelp                  : String,
    val review                  : ArrayList<Review>,
    val harga_vaccine_kucing    : ArrayList<Harga>,
    val harga_vaccine_anjing    : ArrayList<Harga>,
    var distance                : Double,
) : Parcelable

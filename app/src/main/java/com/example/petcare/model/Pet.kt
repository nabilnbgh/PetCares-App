package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pet(
    val id_account  : String,
    var nama_hewan  : String,
    var bb_hewan    : String,
    var tb_hewan    : String,
    var foto_hewan  : Int
) : Parcelable

package com.example.petcare.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user_reminder_table")
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    var pet_name : String,
    var reminder_text : String,
    var year : Int,
    var month : Int,
    var day : Int,
    var hour : Int,
    var minute : Int,
)  : Parcelable
package com.example.petcare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id_account : String,
    val nama_chat : String,
    val foto_chat : String,
    val list_chat : ArrayList<Message>
) :Parcelable

@Parcelize
data class Message(
    val chat_pengirim : String,
    val time : String?,
    val tag : Int,
) : Parcelable

@Parcelize
data class SendChat(
    val id_account : String,
    val nama_chat : String,
    val message   : Message,
) :Parcelable
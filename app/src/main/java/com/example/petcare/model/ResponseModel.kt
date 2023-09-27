package com.example.petcare.model

data class ResponseModel<T>(
    val success     : Boolean,
    val message     : String,
    val data        : T
)
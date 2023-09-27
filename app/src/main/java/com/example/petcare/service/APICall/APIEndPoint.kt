package com.example.petcare.service.APICall

import com.example.petcare.model.Chat
import com.example.petcare.model.GroomingItem
import com.example.petcare.model.Pet
import com.example.petcare.model.Profil
import com.example.petcare.model.VaccineLocation
import com.example.petcare.model.VolunteerLocation
import com.example.petcare.room.Reminder
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIEndPoint {
    // user register
    @POST("api/users/register")
    fun userRegister(
        @Body data : RequestBody
    ) : Call<Profil>


    @POST("api/users/login")
    fun userLogin(
        @Body data : RequestBody
    ): Call<Profil>

    @GET("api/groom/")
    fun getAllGroomPlace(): Call<ArrayList<GroomingItem>>

    @GET("api/volun/")
    fun getAllVolunteerLoc(): Call<ArrayList<VolunteerLocation>>

    @GET("api/vaccine")
    fun getAllVaccineLoc(): Call<ArrayList<VaccineLocation>>

    @GET("api/chat/{id}")
    fun getAllChat(
        @Path("id") id  : String
    ): Call<ArrayList<Chat>>

    @GET("api/pet/{id}")
    fun getAllPetProfile(
        @Path("id") id : String
    ): Call<ArrayList<Pet>>

    @GET("api/reminder/")
    fun getAllReminder(): Call<ArrayList<Reminder>>


    @POST("api/chat/newchat")
    fun newChat(
        @Body data : RequestBody
    ): Call<Chat>

    @POST("api/chat/sendchat")
    fun sendChat(
        @Body data : RequestBody
    ): Call<Chat>

    @POST("api/pet")
    fun sendPetProfile(
        @Body data : RequestBody
    ): Call<Pet>


    @POST("api/reminder/savereminder")
    fun sendReminder(
        @Body data : RequestBody
    ): Call<Reminder>

    @POST("api/users/edituser")
    fun sendEditUser(
        @Body data : RequestBody
    ): Call<Profil>

    @POST("api/pet/deletepet")
    fun deletePetProfile(
        @Body data : RequestBody
    ): Call<Pet>
}
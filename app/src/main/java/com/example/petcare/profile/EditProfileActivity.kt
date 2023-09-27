package com.example.petcare.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petcare.databinding.ActivityEditProfileBinding
import com.example.petcare.model.EditProfil
import com.example.petcare.model.Profil
import com.example.petcare.service.APICall.APIService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var preferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        preferences = getSharedPreferences("userData", MODE_PRIVATE)
        val name = preferences.getString("nama","namauser")
        val noTelp = preferences.getString("notelp", "-")
        Log.d("ID ini boss", preferences.getString("id", "ga ada id").toString())
        val nameForm = binding.nameInputForm
        val telForm = binding.telnumberInputForm

        nameForm.setText(name)
        telForm.setText(noTelp)


        binding.saveProfileButton.setOnClickListener {
            if(binding.nameInputForm.text.toString() != preferences.getString("nama","") || binding.telnumberInputForm.text.toString() != preferences.getString("notelp","")) {
                binding.saveProfileButton.isEnabled = false
                // call API
                callAPI()
            }
        }
        setContentView(binding.root)
    }


    private fun callAPI(){
            val profil = EditProfil(preferences.getString("email", "-")!!,binding.nameInputForm.text.toString(),binding.telnumberInputForm.text.toString())
            val json = Gson().toJson(profil)
            val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            APIService.endpoint.sendEditUser(requestBody).enqueue(
                object : Callback<Profil>{
                    override fun onResponse(call: Call<Profil>, response: Response<Profil>) {
                        preferences.edit().putString("nama", binding.nameInputForm.text.toString()).apply()
                        preferences.edit().putString("notelp", binding.telnumberInputForm.text.toString())
                            .apply()
                        binding.saveProfileButton.isEnabled = true
                        finish()
                    }

                    override fun onFailure(call: Call<Profil>, t: Throwable) {
                        Toast.makeText(this@EditProfileActivity,"Terjadi kesalahan, cek koneksi", Toast.LENGTH_SHORT).show()
                    }
                }
            )
    }
}
package com.example.petcare.petprofile

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.R
import com.example.petcare.databinding.ActivityPetProfileBinding
import com.example.petcare.model.Pet
import com.example.petcare.service.APICall.APIService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PetProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPetProfileBinding
    private lateinit var adapter : AvatarAdapter
    private lateinit var avatarList : ArrayList<Int>
    private lateinit var preferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences("userData", MODE_PRIVATE)
        binding = ActivityPetProfileBinding.inflate(layoutInflater)

        avatarList = setAvatarList()
        adapter = AvatarAdapter(avatarList)
        val petAvaRV = binding.petAvatarRV
        petAvaRV.layoutManager  = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        petAvaRV.adapter = adapter

        binding.savePetProfileButton.setOnClickListener {
            val nama_hewan = binding.nameInputForm.text.toString()
            val berat_hewan = binding.petWeightTextInput.text.toString()
            val tinggi_hewan = binding.petHeightTextInput.text.toString()
            val foto_hewan = adapter.getSelectedPosition()

            if(nama_hewan != ""){
                val id = preferences.getString("id", "null").toString()
                val pet = Pet(id,nama_hewan,berat_hewan,tinggi_hewan,foto_hewan)
                val json = Gson().toJson(pet)
                val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                APIService.endpoint.sendPetProfile(requestBody).enqueue(object : Callback<Pet>{
                    override fun onResponse(call: Call<Pet>, response: Response<Pet>) {
                        if(response.isSuccessful){
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<Pet>, t: Throwable) {
                        Toast.makeText(this@PetProfileActivity,"Terjadi error cek jaringan Anda", Toast.LENGTH_SHORT).show()
                    }

                })
            }

        }
        setContentView(binding.root)
    }

    private fun setAvatarList() : ArrayList<Int>{
        val tempArray = ArrayList<Int>()
        tempArray.add(R.drawable.avatar_cat_1)
        tempArray.add(R.drawable.avatar_cat_2)
        tempArray.add(R.drawable.avatar_cat_3)
        tempArray.add(R.drawable.avatar_cat_4)
        tempArray.add(R.drawable.avatar_cat_5)
        tempArray.add(R.drawable.avatar_cat_6)
        tempArray.add(R.drawable.avatar_cat_7)
        tempArray.add(R.drawable.avatar_cat_8)
        tempArray.add(R.drawable.avatar_cat_9)
        tempArray.add(R.drawable.avatar_dog_1)
        tempArray.add(R.drawable.avatar_dog_2)
        tempArray.add(R.drawable.avatar_dog_3)
        tempArray.add(R.drawable.avatar_dog_4)
        return tempArray
    }

}
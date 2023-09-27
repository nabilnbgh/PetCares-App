package com.example.petcare.petprofile

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petcare.R
import com.example.petcare.databinding.ActivityPetProfileDetailBinding
import com.example.petcare.model.Pet
import com.example.petcare.service.APICall.APIService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PetProfileDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPetProfileDetailBinding
    private lateinit var petProfileItem: Pet
    private var isInRequest : Boolean = false
    private lateinit var avatarList : ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetProfileDetailBinding.inflate(layoutInflater)
        petProfileItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("petprofile_item", Pet::class.java)
        } else {
            intent.getParcelableExtra("petprofile_item")
        } as Pet

        avatarList = setAvatarList()
        binding.deletePetProfileButton.setOnClickListener {
            showDeleteDialog(petProfileItem)
        }
        binding.petNameText.text = petProfileItem.nama_hewan
        binding.petHeightTextView.text = if(petProfileItem.tb_hewan != "") petProfileItem.tb_hewan else "-"
        binding.petWeightTextView.text = if(petProfileItem.bb_hewan != "") petProfileItem.bb_hewan else "-"
        if(petProfileItem.foto_hewan != -1) binding.petProfileImage.setImageResource(avatarList[petProfileItem.foto_hewan])
        setContentView(binding.root)
    }

    private fun showDeleteDialog(item : Pet) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Hapus Hewan")
            .setMessage("Apakah Anda ingin menghapus hewan ini?")
            .setPositiveButton("Delete") { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton("Cancel", null)
            .create()
        alertDialog.show()
    }



    private fun deleteItem(item: Pet){
        isInRequest = true
        val json = Gson().toJson(item)
        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        APIService.endpoint.deletePetProfile(requestBody).enqueue(
            object : Callback<Pet> {
                override fun onResponse(call: Call<Pet>, response: Response<Pet>) {
                    if(response.isSuccessful){
                        finish()
                        isInRequest = false
                    }else{
                        isInRequest = false
                    }

                }
                override fun onFailure(call: Call<Pet>, t: Throwable) {
                    Toast.makeText(this@PetProfileDetailActivity,"Tidak dapat menghibungi ke server, mohon cek koneksi internet",
                        Toast.LENGTH_SHORT).show()
                    isInRequest = false
                }

            }
        )
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
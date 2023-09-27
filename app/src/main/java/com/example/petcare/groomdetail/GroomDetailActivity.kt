package com.example.petcare.groomdetail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.example.petcare.R
import com.example.petcare.databinding.ActivityGroomDetailBinding
import com.example.petcare.model.DummyData
import com.example.petcare.model.GroomingItem
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson


class GroomDetailActivity : AppCompatActivity(){
    private lateinit var binding : ActivityGroomDetailBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //data
        binding = ActivityGroomDetailBinding.inflate(layoutInflater)
        val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("item_detail", GroomingItem::class.java)
        } else {
            intent.getParcelableExtra("item_detail")
        } as GroomingItem

        //binding phone
        binding.phoneButton.setOnClickListener {
            val number = userData.notelp
            if(number != ""){
                val uri = "tel:$number"
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(uri)
                startActivity(intent)
            }
        }
        //binding instagram
        binding.instagramButton.setOnClickListener {
            val username = userData.username
            if(username != ""){
                val uri = Uri.parse("http://instagram.com/_u/$username")
                val likeIng = Intent(Intent.ACTION_VIEW, uri)

                likeIng.setPackage("com.instagram.android")

                try {
                    startActivity(likeIng)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/$username")
                        )
                    )
                }
            }
        }
        //binding whatsapp
        binding.whatsappButton.setOnClickListener {
            val number = userData.notelp
            if(number != ""){
                val url = "https://api.whatsapp.com/send?phone=$number"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }


        binding.jamOperText.text = userData.jam_operasional
        // alamat & foto lokasi
        binding.alamatLokasi.text = userData.alamat
        if(userData.foto_lokasi != ""){
            val bitmapImg = decodeImage(userData.foto_lokasi)
            binding.fotoLokasi.setImageBitmap(bitmapImg)
        }else{
            binding.fotoLokasi.setImageResource(R.drawable.no_image)
        }

        //data for antar hewan
        preferences = getSharedPreferences("storeData", MODE_PRIVATE)
        val storeData = DummyData(userData.alamat,userData.latitude,userData.longitude)
        var prefEdit = preferences.edit()
        val gson = Gson()
        var json = gson.toJson(storeData)
        prefEdit.putString("storeData",json).apply()


        preferences = getSharedPreferences("userData", MODE_PRIVATE)
        prefEdit = preferences.edit()
        json = gson.toJson(userData)
        prefEdit.putString("userData",json).apply()



        //view pager fragment
        val adapter = GroomPagerAdapter(supportFragmentManager,lifecycle)
        binding.groomViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout,binding.groomViewPager){tab,position ->
            when(position){
                0 -> tab.text = "Lokasi"
                1 -> tab.text = "Harga"
                else -> tab.text = "Ulasan"
            }
        }.attach()

        setContentView(binding.root)
    }
    private fun decodeImage(image : String) : Bitmap {
        val imageBytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
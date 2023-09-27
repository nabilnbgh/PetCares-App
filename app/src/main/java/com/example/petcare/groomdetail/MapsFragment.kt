package com.example.petcare.groomdetail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.petcare.databinding.FragmentMapsBinding
import com.example.petcare.dummy.DummyActivity
import com.example.petcare.mainpage.ChatActivity
import com.example.petcare.model.Chat
import com.example.petcare.model.GroomingItem
import com.example.petcare.model.Message
import com.example.petcare.service.APICall.APIService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsFragment : Fragment(), OnMapReadyCallback{
    private var _binding : FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences : SharedPreferences
    private lateinit var userData : GroomingItem

    private lateinit var mMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapsBinding.inflate(inflater, container,false)
        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        val json = preferences.getString("userData","")
        userData = Gson().fromJson(json,GroomingItem::class.java)

        binding.drivePetButton.setOnClickListener {
            val intent = Intent(requireContext(),DummyActivity::class.java)
            startActivity(intent)
        }
        binding.groomChatButton.setOnClickListener {
            val id = preferences.getString("id","null").toString()
            val nama_chat = userData.nama_lokasi
            val list_chat = ArrayList<Message>()
            val json = Gson().toJson(Chat(id,nama_chat,userData.foto_lokasi,list_chat))
            val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            APIService.endpoint.newChat(requestBody).enqueue(object : Callback<Chat>{
                override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                    if(response.isSuccessful){
                        Log.d("new Chat is success", response.body().toString())
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra("item_detail", response.body())
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(requireContext(),"Terjadi error",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Chat>, t: Throwable) {
                    Toast.makeText(requireContext(),"Terjadi error cek jaringan Anda",Toast.LENGTH_SHORT).show()
                }

            })
        }

        //google map
        val mapFragment =childFragmentManager.findFragmentById(com.example.petcare.R.id.groomMaps) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val storeLoc = LatLng(userData.latitude.toDouble(),userData.longitude.toDouble())
        mMap.addMarker(
            MarkerOptions()
            .position(storeLoc)
            .title("Lokasi Toko"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLoc, 20.0f))
    }


}
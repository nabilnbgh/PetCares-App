package com.example.petcare.dummy

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.petcare.R
import com.example.petcare.databinding.ActivityDummyBinding
import com.example.petcare.model.DummyData
import com.example.petcare.model.distance.GoogleMapDTO
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class DummyActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding : ActivityDummyBinding
    private lateinit var mMap: GoogleMap
    private lateinit var preferences : SharedPreferences
    private lateinit var dummyData : DummyData
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var storeLoc : LatLng
    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_LOCATION = 99
    private var lastKnownLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDummyBinding.inflate(layoutInflater)
        preferences = getSharedPreferences("storeData", MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val json = preferences.getString("storeData","")
        dummyData = Gson().fromJson(json,DummyData::class.java)
        storeLoc = LatLng(dummyData.latitude,dummyData.longitude)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                showAlertPermission()
            }
            else{
                requestLocationPermission()
            }
        }else{
            locationPermissionGranted = true
        }


        binding.layananDummyButton.setOnClickListener {
            Toast.makeText(this,"Order driver berhasil!", Toast.LENGTH_SHORT).show()
        }
        //google map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.dummyMaps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setContentView(binding.root)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        setDestinationUI()
        getCurrentLocation()

    }


    private fun setDestinationUI(){
        //set data to UI
        binding.endDestiText.text =  dummyData.alamat

    }

    private fun showAlertPermission(){
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setMessage("Aplikasi membutuhkan akses untuk melihat lokasimu")
            .setTitle("Permintaan Izin")
            .setCancelable(false)
            .setPositiveButton("OK"
            ) { _, _ -> requestLocationPermission() }.create().show()
    }
    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),PERMISSIONS_REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when(requestCode){
            PERMISSIONS_REQUEST_LOCATION -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationPermissionGranted = true

                }else{
                    Toast.makeText(this,"Tidak ada izin lokasi, tolong ubah izin lokasi",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        // do anything here after location granted
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        try {
            if (locationPermissionGranted) {
                Log.d("TAG", "location granted true ")
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            Log.d("Last Know Location", "getCurrentLocation: Lastt know Location ada")
                            val  userLoc = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                            Log.d("Last Know Location", userLoc.toString())
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(userLoc)
                                    .title("Lokasi Anda"))
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(storeLoc)
                                    .title("Lokasi Toko"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 20.0f))
                            val url = getDirectionURL(storeLoc,userLoc)
                            getDirection(url)
                        }else{
                            Log.d("Last Know Location", "getCurrentLocation: Lastt know Location null")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Exception Triggered", "getCurrentLocation: ",e )
        }
    }

    private fun getDirectionURL(origin : LatLng , destination : LatLng) : String{
         return "https://maps.googleapis.com/maps/api/directions/json?destination=${destination.latitude},${destination.longitude}&origin=${origin.latitude},${origin.longitude}&key=AIzaSyC6NVjYhb8NF3Zh44iOy5GJVPoB_lCQSAo"
    }

    private fun getDirection(url : String){
        CoroutineScope(Dispatchers.IO).launch{
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            val data = response.body?.string()
            val result = ArrayList<List<LatLng>>()
            if (data != null) {
                try{
                    val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)

                    val path =  ArrayList<LatLng>()

                    for (i in 0 until respObj.routes[0].legs[0].steps.size){
                        path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    }
                    result.add(path)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                val lineoption = PolylineOptions()
                for (i in result.indices){
                    lineoption.addAll(result[i])
                    lineoption.width(10f)
                    lineoption.color(Color.BLUE)
                    lineoption.geodesic(true)
                }
                mMap.addPolyline(lineoption)
            }


        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }
}
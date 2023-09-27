package com.example.petcare.volunteer

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.R
import com.example.petcare.databinding.ActivityVolunteerBinding
import com.example.petcare.dummy.DummyActivity
import com.example.petcare.mainpage.ChatActivity
import com.example.petcare.model.Chat
import com.example.petcare.model.DummyData
import com.example.petcare.model.Message
import com.example.petcare.model.VolunteerLocation
import com.example.petcare.service.APICall.APIService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class VolunteerActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var binding : ActivityVolunteerBinding
    private lateinit var preferences : SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val defaultLocationBandung = LatLng(6.9175, 107.6191)
    private lateinit var voluLoc : ArrayList<VolunteerLocation>
    private val PERMISSIONS_REQUEST_LOCATION = 99
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private var map: GoogleMap? = null
    private var disabledMarker : Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setArrayVolunteerLocation()

        binding.nearestListButton.setOnClickListener {
            val bottomSheetLayoutRV = findViewById<ConstraintLayout>(R.id.bottomSheetRVLayout)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutRV)
            bottomSheetLayoutRV.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            it.visibility = View.INVISIBLE
        }
        //check location permission
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

        //granted location permission
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.groomMaps) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        setBottomSheetLayoutCallback()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d("DEBUG", "onMapReady: enter")
        getDeviceLocation()
    }
    private fun showAlertPermission(){
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setMessage("Aplikasi membutuhkan akses untuk melihat lokasimu")
            .setTitle("Permintaan Izin")
            .setCancelable(false)
            .setPositiveButton("OK", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    requestLocationPermission()
                }
            }).create().show()
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
                    getDeviceLocation()
                }else{
                    Toast.makeText(this,"Tidak ada izin lokasi, tolong ubah izin lokasi",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        // do anything here after location granted
    }


    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Log.d("TAG", "location granted true ")
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), 20f))
                            calculateDistance(lastKnownLocation!!)
                            setMarker(map!!)
                            setUserMarker(map!!,
                                LatLng(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)
                            )
                            setNearestRV()

                        }
                    } else {
                        Log.d("TAG", "location granted false ")
                        Log.d("VolunteerAcvitiy", "Current location is null. Using defaults.")
                        map?.moveCamera(
                            CameraUpdateFactory
                            .newLatLngZoom(defaultLocationBandung, 20f))
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun setNearestRV(){
        val nearestRV = binding.bottomSheetRVLayout.nearestVolunteerRV
        nearestRV.visibility = View.VISIBLE
        nearestRV.layoutManager = LinearLayoutManager(this)
        val adapter = VolunteerAdapter(voluLoc)
        adapter.setOnClickListener(
            object : VolunteerAdapter.OnClickListener{
                override fun onClick(position: Int, item: VolunteerLocation) {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(item.latitude, item.longitude), 20f))
                }
            }
        )
        nearestRV.adapter = adapter
    }

    private fun setUserMarker(googleMap: GoogleMap, position : LatLng){
        val bitmapIcon = getBitmapFromVectorDrawable(this, R.drawable.userpin)
        disabledMarker = googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon))
        )
        googleMap.uiSettings.isMapToolbarEnabled = false
    }
    private fun setArrayVolunteerLocation(){
        val tempArray = ArrayList<VolunteerLocation>()
        tempArray.add(
            VolunteerLocation(
                "Cat & Dog Hotel",
                "Jl. Kembar Mas Utara I, Ancol, Kec. Regol, Kota Bandung, Jawa Barat 40254",
                4.5,
                -6.944343,
                107.618757,
                3,
                0.0,
                R.drawable.tempat_1,
            )
        )
        tempArray.add(
            VolunteerLocation(
                "N & I Pet Hotel",
                "Jl. Citepus II, Pajajaran, Kec. Cicendo, Kota Bandung, Jawa Barat 40173",
                4.2,
                -6.901227,
                107.58833,
                3,
                0.0,
                R.drawable.tempat_2,
            )
        )
        tempArray.add(
            VolunteerLocation(
                "Pet Care & Hotel",
                "Jl. Payung Kencana, Suka Asih, Kec. Bojongloa Kaler, Kota Bandung, Jawa Barat 40231",
                4.0,
                -6.934575,
                107.587443,
                3,
                0.0,
                R.drawable.tempat_3,

            )
        )
        tempArray.add(
            VolunteerLocation(
                "Pet Hotel",
                "JL Insinyur Haji Juanda, Gang Dago Jati I, Dago, Kecamatan Coblong, Kota Bandung, Jawa Barat 66274",
                4.7,
                -6.877297,
                107.618974,
                3,
                0.0,
                R.drawable.tempat_4,
            )
        )
        tempArray.add(
            VolunteerLocation(
                "Naya Shintia",
                "Jl. Indramayu 2, Antapani Kidul, Kec. Antapani, Kota Bandung, Jawa Barat 40291",
                4.1,
                -6.917788,
                107.658387,
                1,
                0.0,
                R.drawable.voluntir_1,
            )
        )
        tempArray.add(
            VolunteerLocation(
                "Anita Sabrina",
                "Jl. Sukanagara, Antapani Kidul, Kec. Antapani, Kota Bandung, Jawa Barat 40291",
                4.6,
                -6.921349,
                107.659935,
                1,
                0.0,
                R.drawable.voluntir_2,
            )
        )
        tempArray.add(
            VolunteerLocation(
                "Dodi Sucipto",
                "Jl. Andromeda VII, Sekejati, Kec. Buahbatu, Kota Bandung, Jawa Barat 40286",
                4.3,
                -6.951443,
                107.658057,
                1,
                0.0,
                R.drawable.voluntir_3,

            )
        )
        tempArray.add(
            VolunteerLocation(
                "Sandra Firdaus",
                "Jl. Setrasari Kulon II, Sukarasa, Kec. Sukasari, Kota Bandung, Jawa Barat 40152",
                4.5,
                -6.876605,
                107.585845,
                2,
                0.0,
                R.drawable.voluntir_4,
            )
        )
        tempArray.add(
            VolunteerLocation(
                "Inu Care",
                "Jl. Riung Saluyu A IX, Cisaranten Kidul, Kec. Gedebage, Kota Bandung, Jawa Barat 40292",
                4.5,
                -6.951519,
                107.68357,
                2,
                0.0,
                -1,
            )
        )
        tempArray.add(
            VolunteerLocation(
                "Dog Care",
                "Jl. Panday, Pasir Biru, Kec. Cibiru, Kota Bandung, Jawa Barat 40615",
                4.5,
                -6.920295,
                107.723254,
                2,
                0.0,
                -1,
            )
        )

        voluLoc = tempArray
    }


    private fun calculateDistance(lastKnowLocation : Location){
        voluLoc.forEach {
            val dist = distance(
                lastKnowLocation.latitude,
                lastKnowLocation.longitude,
                it.latitude,
                it.longitude
            )
            it.distance = dist
        }
        voluLoc.sortBy{ it.distance}
    }

    private fun setMarker(googleMap : GoogleMap){

        voluLoc.forEach {
            val bitmapIcon = when(it.jenis_voluntir){
                1 ->{
                    getBitmapFromVectorDrawable(this, R.drawable.cat_pin)
                }
                2->{
                    getBitmapFromVectorDrawable(this, R.drawable.dog_pin)
                }else -> {
                getBitmapFromVectorDrawable(this, R.drawable.catdog_pin)
                }
            }
            val latlng = LatLng(it.latitude,it.longitude)
            val marker = googleMap.addMarker(MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon)))
            marker!!.tag = voluLoc.indexOf(it)
            googleMap.setOnMarkerClickListener(this)
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515 * 1.609344
        //distance in KM
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
    private fun setBottomSheetLayoutCallback(){
        val bottomSheetLayoutRV = findViewById<ConstraintLayout>(R.id.bottomSheetRVLayout)
        val bottomSheetRVBehavior = BottomSheetBehavior.from(bottomSheetLayoutRV)
        val bottomSheetLayout = findViewById<ConstraintLayout>(R.id.bottomSheetLayout)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        val callback = object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.nearestListButton.visibility = View.VISIBLE
                }else{
                    binding.nearestListButton.visibility = View.INVISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //no need to implement
            }
        }
        bottomSheetRVBehavior.addBottomSheetCallback(callback)
        bottomSheetBehavior.addBottomSheetCallback(callback)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        binding.nearestListButton.visibility = View.INVISIBLE
        val bottomSheetLayout = findViewById<ConstraintLayout>(R.id.bottomSheetLayout)
        val bottomSheetLayoutRV = findViewById<ConstraintLayout>(R.id.bottomSheetRVLayout)
        if(p0 == disabledMarker) {
            binding.nearestListButton.visibility = View.VISIBLE
            bottomSheetLayout.visibility = View.INVISIBLE
            bottomSheetLayoutRV.visibility = View.VISIBLE
            return true
        }else{
            if(p0.tag != null){
                val volunteerNameTitleText = bottomSheetLayout.findViewById<TextView>(R.id.volunteerNameTitleText)
                val volunteerNameText = bottomSheetLayout.findViewById<TextView>(R.id.volunteerNameText)
                val volunteerTypeText = bottomSheetLayout.findViewById<TextView>(R.id.volunteerTypeText)
                val volunteerDistanceText = bottomSheetLayout.findViewById<TextView>(R.id.volunteerDistanceText)
                val volunteerRatingText = bottomSheetLayout.findViewById<TextView>(R.id.volunteerRatingText)
                val volunteerTypeImage = bottomSheetLayout.findViewById<ImageView>(R.id.volunteerTypeImage)
                val volunteerTypeImage2 = bottomSheetLayout.findViewById<ImageView>(R.id.volunteerTypeImage2)
                val driverPetButton  = bottomSheetLayout.findViewById<AppCompatButton>(R.id.drivePetVolunteerButton)
                val volunteerChatButton  = bottomSheetLayout.findViewById<AppCompatButton>(R.id.volunteerChatButton)

                val idx = p0.tag as Int
                preferences = getSharedPreferences("storeData", MODE_PRIVATE)
                val storeData = DummyData(voluLoc.elementAt(idx).alamat,voluLoc.elementAt(idx).latitude,voluLoc.elementAt(idx).longitude)
                val prefEdit = preferences.edit()
                val gson = Gson()
                var json = gson.toJson(storeData)
                prefEdit.putString("storeData",json).apply()
                driverPetButton.setOnClickListener {
                    val intent = Intent(this, DummyActivity::class.java)
                    startActivity(intent)
                }

                volunteerChatButton.setOnClickListener {
                    preferences = getSharedPreferences("userData", MODE_PRIVATE)
                    val id = preferences.getString("id","null").toString()
                    val nama_chat = volunteerNameText.text.toString()
                    val list_chat = ArrayList<Message>()
                    val json = Gson().toJson(Chat(id,nama_chat, "",list_chat))
                    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    APIService.endpoint.newChat(requestBody).enqueue(object : Callback<Chat>{
                        override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                            if(response.isSuccessful){
                                Log.d("new Chat is success", response.body().toString())
                                val intent = Intent(this@VolunteerActivity, ChatActivity::class.java)
                                intent.putExtra("item_detail", response.body())
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this@VolunteerActivity,"Terjadi error",Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Chat>, t: Throwable) {
                            Toast.makeText(this@VolunteerActivity,"Terjadi error cek jaringan Anda",Toast.LENGTH_SHORT).show()
                        }

                    })
                }

                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

                if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                bottomSheetLayoutRV.visibility = View.INVISIBLE
                bottomSheetLayout.visibility = View.VISIBLE
                volunteerNameTitleText.text = "Nama Voluntir"
                volunteerNameText.text = voluLoc.elementAt(idx).nama_tempat_voluntir
                when(voluLoc.elementAt(idx).jenis_voluntir){
                    1 ->{
                        volunteerTypeImage.setImageResource(R.drawable.catvol)
                        volunteerTypeImage2.visibility = View.GONE
                        volunteerTypeText.text = "Kucing"
                    }
                    2->{

                        volunteerTypeImage.setImageResource(R.drawable.dogvol)
                        volunteerTypeImage2.visibility = View.GONE
                        volunteerTypeText.text = "Anjing"
                    }else -> {
                    volunteerTypeImage.setImageResource(R.drawable.dogvol)
                    volunteerTypeImage2.visibility = View.VISIBLE
                    volunteerTypeImage2.setImageResource(R.drawable.catvol)
                    volunteerTypeText.text = "Kucing/Anjing"
                    }
                }
                volunteerRatingText.text = "%.1f".format(voluLoc.elementAt(idx).rating)
                volunteerDistanceText.text = "%.2f".format(voluLoc.elementAt(idx).distance) + " km"
                return true
            }
            bottomSheetLayout.visibility = View.INVISIBLE
            bottomSheetLayoutRV.visibility = View.VISIBLE
            return false
        }

    }



}
package com.example.petcare.vaccine

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.databinding.ActivityVaccineBinding
import com.example.petcare.model.VaccineLocation
import com.example.petcare.service.APICall.APIService
import com.example.petcare.service.Distance
import com.example.petcare.vaccinedetail.VaccineDetailActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VaccineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVaccineBinding
    private var vaccineLocation = ArrayList<VaccineLocation>()
    private lateinit var adapter : VaccineItemAdapter
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_LOCATION = 99
    private var lastKnownLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaccineBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        adapter = VaccineItemAdapter(vaccineLocation)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                showAlertPermission()
            }
            else{
                requestLocationPermission()
            }
        }else{
            locationPermissionGranted = true
            callVaccineAPI()

            setContentView(binding.root)
        }

        setContentView(binding.root)
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
                    callVaccineAPI()
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

//            val mLocationRequest = LocationRequest
//                .Builder(1000)
//                .setPriority(Priority.PRIORITY_HIGH_ACCURACY) as LocationRequest
//            val mLocationCallback: LocationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    if (locationResult == null) return
//                }
//            }
//            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)


            if (locationPermissionGranted) {
                Log.d("TAG", "location granted true ")
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            Log.d("test", "getCurrentLocation:  terpanggil")
                            val newList = calculateDistance()
                            setNewVaccineRV(newList)
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun setNewVaccineRV(newList : ArrayList<VaccineLocation>){
       adapter.setRVmWithDistance(newList)
    }

    private fun calculateDistance() : ArrayList<VaccineLocation>{
        val tempList = vaccineLocation
        val distance = Distance(lastKnownLocation!!.latitude,lastKnownLocation!!.longitude)
        tempList.forEach {
            distance.setEndLocation(it.latitude,it.longitude)
            it.distance = distance.calculatedistance()
        }
        tempList.sortBy {
            it.distance
        }
        return tempList
    }
    private fun callVaccineAPI(){
        APIService.endpoint.getAllVaccineLoc().enqueue(object : Callback<ArrayList<VaccineLocation>> {
            override fun onResponse(
                call: Call<ArrayList<VaccineLocation>>,
                response: Response<ArrayList<VaccineLocation>>
            ) {
                if(response.isSuccessful){
                    vaccineLocation = response.body()!!
                    setRecyclerView()
                    getCurrentLocation()
                    setSearchView()
                }
            }

            override fun onFailure(call: Call<ArrayList<VaccineLocation>>, t: Throwable) {

                Log.e("error", t.message!!, t)
            }
        })
    }


    private fun setRecyclerView(){
        val recyclerView = binding.vaccineRV
        recyclerView.layoutManager = LinearLayoutManager(this@VaccineActivity)
        adapter = VaccineItemAdapter(vaccineLocation)
        recyclerView.adapter = adapter
        adapter.setOnClickListener(object : VaccineItemAdapter.OnClickListener{
            override fun onClick(position: Int, item: VaccineLocation) {
                val intent = Intent(this@VaccineActivity, VaccineDetailActivity::class.java)
                intent.putExtra("item_detail", item)
                startActivity(intent)
            }
        }
        )
    }


    private fun setSearchView(){
        val searchview = binding.vaccineSearchView

        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0 != null){
                    filterSearch(p0)
                }
                return true
            }

        })
    }
    private fun filterSearch(query : String){
        val filteredList = ArrayList<VaccineLocation>()
        vaccineLocation.forEach { vaccineLoc ->
            if(vaccineLoc.nama_lokasi.lowercase().contains(query.lowercase())){
                filteredList.add(vaccineLoc)
            }
        }

        if(filteredList.isNotEmpty()){
            adapter.setFilteredList(filteredList)
        }
    }


}
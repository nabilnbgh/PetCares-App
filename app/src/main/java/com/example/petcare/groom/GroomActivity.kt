package com.example.petcare.groom

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
import com.example.petcare.databinding.ActivityGroomBinding
import com.example.petcare.groomdetail.GroomDetailActivity
import com.example.petcare.model.GroomingItem
import com.example.petcare.service.APICall.APIService
import com.example.petcare.service.Distance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroomActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGroomBinding
    private var groomingItemList = ArrayList<GroomingItem>()
    private lateinit var adapter : GroomItemAdapter
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var locationPermissionGranted = false
    private val PERMISSIONS_REQUEST_LOCATION = 99
    private var lastKnownLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroomBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        adapter = GroomItemAdapter(groomingItemList)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                showAlertPermission()
            }
            else{
                requestLocationPermission()
            }
        }else{
            locationPermissionGranted = true
            callGroomAPI()
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
                    callGroomAPI()

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
                            Log.d("test", "getCurrentLocation:  terpanggil")
                            val newList = calculateDistance()
                            setNewRV(newList)
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
    }


    private fun callGroomAPI(){
        APIService.endpoint.getAllGroomPlace().enqueue(object :Callback<ArrayList<GroomingItem>>{
            override fun onResponse(
                call: Call<ArrayList<GroomingItem>>,
                response: Response<ArrayList<GroomingItem>>
            ) {
                groomingItemList = response.body()!!
                setRecyclerView(groomingItemList)
                getCurrentLocation()
                setSearchView()


            }

            override fun onFailure(call: Call<ArrayList<GroomingItem>>, t: Throwable) {

                Log.e("error", t.message!!, t)
            }
        }
        )
    }
    private fun setRecyclerView(groomingItemList : ArrayList<GroomingItem>){
        val recyclerView = binding.groomingRV
        recyclerView.layoutManager = LinearLayoutManager(this@GroomActivity)
        adapter = GroomItemAdapter(groomingItemList)
        recyclerView.adapter = adapter
        adapter.setOnClickListener(object : GroomItemAdapter.OnClickListener{
            override fun onClick(position: Int, item: GroomingItem) {
                val intent = Intent(this@GroomActivity, GroomDetailActivity::class.java)
                Log.d("Groom Detail Item Detai", item.toString())
                intent.putExtra("item_detail", item)
                startActivity(intent)
            }
        }
        )
    }
    private fun setSearchView(){
        val searchview = binding.groomSearchView

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
        val filteredList = ArrayList<GroomingItem>()
        groomingItemList.forEach { groomingItem ->
            if(groomingItem.nama_lokasi.lowercase().contains(query.lowercase())){
                filteredList.add(groomingItem)
            }
        }

        if(filteredList.isNotEmpty()){
            adapter.setFilteredList(filteredList)
        }
    }
    private fun setNewRV(newList : ArrayList<GroomingItem>){
        adapter.setRVmWithDistance(newList)
    }

    private fun calculateDistance() : ArrayList<GroomingItem>{
        val tempList = groomingItemList
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



}
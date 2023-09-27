package com.example.petcare.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.MainActivity
import com.example.petcare.databinding.FragmentProfileBinding
import com.example.petcare.mainpage.ReminderDetailActivity
import com.example.petcare.model.Pet
import com.example.petcare.petprofile.PetProfileActivity
import com.example.petcare.petprofile.PetProfileDetailActivity
import com.example.petcare.room.Reminder
import com.example.petcare.room.UserReminderDatabase
import com.example.petcare.service.APICall.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val database by lazy { UserReminderDatabase(requireContext()) }
    private lateinit var preferences: SharedPreferences
    private var petList = ArrayList<Pet>()
    private lateinit var adapter : PetProfileAdapter
    private var name = ""
    private var email = ""
    private var noTelp = ""
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        name = preferences.getString("nama","namauser")!!
        email = preferences.getString("email", "emailuser")!!
        noTelp = preferences.getString("notelp", "-")!!

        val nameForm = binding.nameProfileText
        val emailForm = binding.emailProfileText
        val telForm = binding.notelpProfileText
        emailForm.text = email
        nameForm.text = name
        telForm.text = noTelp

        setPetRV()
        getAllPet()
        binding.profilHewanButton.setOnClickListener {
            startActivity(Intent(requireContext(),PetProfileActivity::class.java))
        }
        binding.profileEditButton.setOnClickListener{
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.profileExitButton.setOnClickListener {
            showLogoutDialog()
        }

        return binding.root
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin akan keluar?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Ya") { _, _ ->
                doLogout()
            }
            .create()
        alertDialog.show()
    }
    private fun doLogout(){
        CoroutineScope(Dispatchers.IO).launch { database.userReminderDao().nukeTable()
            CoroutineScope(Dispatchers.Main).launch{
                preferences.edit().clear().apply()
                preferences = requireActivity().getSharedPreferences("userPreference",AppCompatActivity.MODE_PRIVATE)
                preferences.edit().putBoolean("userPreference",true).apply()
                startActivity(Intent(requireContext(),MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        updateUI()
        getAllPet()
    }

    private fun updateUI(){
        val newName = preferences.getString("nama","namauser")
        val newEmail = preferences.getString("email", "emailuser")
        val newTelp = preferences.getString("notelp", "-")
        val nameForm = binding.nameProfileText
        val emailForm = binding.emailProfileText
        val telForm = binding.notelpProfileText
        emailForm.text = newEmail
        nameForm.text = newName
        telForm.text = if(newTelp == "")  "-" else newTelp
    }

    private fun setPetRV(){
        adapter = PetProfileAdapter(petList)
        adapter.setOnItemClickListener(object : PetProfileAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, item: Pet) {
                showPetProfileDetail(item)
            }
        }
        )
        val petRV = binding.petRecyclerView
        petRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        petRV.adapter = adapter
    }


    private fun updatePetRV(){
        adapter.updateRV(petList)
    }


    private fun showPetProfileDetail(item: Pet){
        val intent = Intent(requireContext(), PetProfileDetailActivity::class.java)
        intent.putExtra("petprofile_item",item)
        startActivity(intent)
    }

    private fun getAllPet(){
        val id = preferences.getString("id", "null").toString()
        APIService.endpoint.getAllPetProfile(id).enqueue(object : Callback<ArrayList<Pet>>{
            override fun onResponse(
                call: Call<ArrayList<Pet>>,
                response: Response<ArrayList<Pet>>
            ) {
                if(response.isSuccessful){
                    petList = response.body()!!
                    //update UI
                    updatePetRV()
                }
            }

            override fun onFailure(call: Call<ArrayList<Pet>>, t: Throwable) {
                Toast.makeText(requireContext(),"Terjadi error cek jaringan Anda", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
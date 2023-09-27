package com.example.petcare.vaccinedetail

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.databinding.FragmentVaccinePriceBinding
import com.example.petcare.model.VaccineLocation
import com.google.gson.Gson


class VaccinePriceFragment : Fragment() {
    private var _binding : FragmentVaccinePriceBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences : SharedPreferences
    private lateinit var userData : VaccineLocation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVaccinePriceBinding.inflate(inflater,container, false)
        // Inflate the layout for this fragment
        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        val json = preferences.getString("userData","")
        userData = Gson().fromJson(json, VaccineLocation::class.java)

        if(userData.harga_vaccine_kucing.isEmpty()){
            binding.vaccineKucingCard.visibility = View.GONE
        }else{
            val recyclerView = binding.vaccineKucingRV
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = VaccinePriceAdapter(userData.harga_vaccine_kucing)
        }
        if(userData.harga_vaccine_anjing.isEmpty()){
            binding.vaccineAnjingCard.visibility = View.GONE
        }else{
            val recyclerView = binding.vaccineAnjingRV
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = VaccinePriceAdapter(userData.harga_vaccine_anjing)
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.petcare.vaccinedetail

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.databinding.FragmentVaccineReviewBinding
import com.example.petcare.model.VaccineLocation
import com.google.gson.Gson

class VaccineReviewFragment : Fragment() {
    private var _binding : FragmentVaccineReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var userData : VaccineLocation
    private lateinit var preferences : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVaccineReviewBinding.inflate(inflater, container, false)

        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        val json = preferences.getString("userData","")
        userData = Gson().fromJson(json, VaccineLocation::class.java)

        Log.d("test123",userData.review.toString())
        // Inflate the layout for this fragment

        val recyclerView = binding.vaccinedetailReviewRV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = VaccineReviewAdapter(userData.review)
        recyclerView.adapter = adapter
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
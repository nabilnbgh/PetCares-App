package com.example.petcare.groomdetail

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.databinding.FragmentReviewBinding
import com.example.petcare.model.GroomingItem
import com.google.gson.Gson


class ReviewFragment : Fragment() {
    private var _binding : FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var userData : GroomingItem
    private lateinit var preferences : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        val json = preferences.getString("userData","")
        userData = Gson().fromJson(json,GroomingItem::class.java)

        Log.d("test123",userData.review.toString())
        // Inflate the layout for this fragment

        val recyclerView = binding.groomingdetailReviewRV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = GroomReviewAdapter(userData.review)
        recyclerView.adapter = adapter
        return binding.root
    }
}
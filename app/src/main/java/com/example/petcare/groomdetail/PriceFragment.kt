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
import com.example.petcare.databinding.FragmentPriceBinding
import com.example.petcare.model.GroomingItem
import com.google.gson.Gson

class PriceFragment : Fragment() {
    private var _binding : FragmentPriceBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences : SharedPreferences
    private lateinit var userData : GroomingItem
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPriceBinding.inflate(inflater,container,false)

        preferences = requireActivity().getSharedPreferences("userData",AppCompatActivity.MODE_PRIVATE)
        val json = preferences.getString("userData","")
        userData = Gson().fromJson(json,GroomingItem::class.java)

        Log.d("userData", userData.toString())
        if(userData.harga_kucing.isEmpty()){
            binding.groomingKucingCard.visibility = View.GONE
        }else{
            val recyclerView = binding.groomingKucingRV
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = GroomPriceAdapter(userData.harga_kucing)
        }
        if(userData.harga_anjing.isEmpty()){
            binding.groomingAnjingCard.visibility = View.GONE
        }else{
            val recyclerView = binding.groomingAnjingRV
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = GroomPriceAdapter(userData.harga_anjing)
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
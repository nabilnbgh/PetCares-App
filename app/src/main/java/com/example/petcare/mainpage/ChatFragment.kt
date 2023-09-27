package com.example.petcare.mainpage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.databinding.FragmentChatBinding
import com.example.petcare.model.Chat
import com.example.petcare.service.APICall.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment() {
    private var _binding : FragmentChatBinding? =null
    private val binding get() = _binding!!
    private var listChat = ArrayList<Chat>()
    private lateinit var adapter : ChatAdapter
    private lateinit var preferences : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        callAPI()

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        Log.d("On Resume Call API", "onResume: ")
        callAPI()
    }

    private fun callAPI(){
        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        val id = preferences.getString("id","null").toString()
        APIService.endpoint.getAllChat(id)
            .enqueue(object : Callback<ArrayList<Chat>>{
                override fun onResponse(
                    call: Call<ArrayList<Chat>>,
                    response: Response<ArrayList<Chat>>
                ) {
                    if(response.isSuccessful){
                        listChat = response.body()!!
                        setRecyclerViewChat()
                    }
                }

                override fun onFailure(call: Call<ArrayList<Chat>>, t: Throwable) {
                    Log.e("ERROR OnFailure", t.message, t )
                }
            })
    }

    private fun setRecyclerViewChat(){
        val chatRV =  binding.chatRecyclerView
        chatRV.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatAdapter(listChat)
        adapter.setOnClickListener(object : ChatAdapter.OnClickListener{
            override fun onClick(position: Int, item: Chat) {
                val intent = Intent(requireContext(),ChatActivity::class.java)
                intent.putExtra("item_detail",item)
                startActivity(intent)
            }
        })
        chatRV.adapter = adapter
    }


}
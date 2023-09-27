package com.example.petcare.mainpage

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.petcare.databinding.ActivityChatBinding
import com.example.petcare.model.Chat
import com.example.petcare.model.Message
import com.example.petcare.model.SendChat
import com.example.petcare.service.APICall.APIService
import com.google.gson.Gson
import com.xwray.groupie.GroupieAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private val adapter = GroupieAdapter()
    private lateinit var preferences : SharedPreferences
    private lateinit var userMessage : Message
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val chat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra("item_detail", Chat::class.java)

        }else {
            intent.getParcelableExtra("item_detail")
        } as Chat
        setDummyChat(chat)

        preferences = getSharedPreferences("userData", MODE_PRIVATE)
            binding.chatRV.adapter = adapter
        binding.sendButton.setOnClickListener {
            val chatText = binding.editTextText.text
            if(chatText.toString() != ""){
                val date = Calendar.getInstance()
                val time = setTimeDateText(date.time)
                userMessage = Message(chatText.toString(),time,1)
                val sendChatItem = SendChatItem(userMessage)
                adapter.add(sendChatItem)
                sendChat(chat,chatText.toString())
            }
            chatText.clear()
        }
        setContentView(binding.root)
    }


    private fun setTimeDateText(date : Date) : String{
        val tf = SimpleDateFormat("h:mm a")
        val time = tf.format(date)
        return time
    }

    private fun setDummyChat(chat : Chat){
        chat.list_chat.forEach {
            if(it.tag == 0) adapter.add(RecieveChatItem(it)) else adapter.add(SendChatItem(it))
        }
    }

    private fun sendChat(chat : Chat, chatText : String){
        val id = preferences.getString("id","null").toString()
        val nama_chat = chat.nama_chat
        val list_chat = userMessage
        val json = Gson().toJson(SendChat(id,nama_chat,list_chat))
        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        APIService.endpoint.sendChat(requestBody).enqueue(object :Callback<Chat>{
            override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                if(response.isSuccessful) Log.d("Send Chat", "Chat sent successfully") else  Log.e("Send Chat", "Chat Error")
            }
            override fun onFailure(call: Call<Chat>, t: Throwable) {
                Log.e("Send Chat", "Ada masalah ketika mengirim chat, cek koneksi", t)
            }

        })
    }
}
package com.example.petcare.mainpage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.Chat
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(val results : ArrayList<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private var onClickListener : ChatAdapter.OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_chat_item, parent, false)
    )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userNameText.text = results[position].nama_chat
        if(!results[position].list_chat.isEmpty()){
            results[position].list_chat.last().chat_pengirim
            var lastMessage = results[position].list_chat.last().chat_pengirim
            if(lastMessage.length > 30){
                lastMessage = lastMessage.substring(0,30) + "..."
            }
            holder.userChatText.text = lastMessage
        } else {
            holder.userChatText.text = ""
        }
        if(results[position].foto_chat != ""){
            val bitMapImg = decodeImage(results[position].foto_chat)
            holder.chatImageView.setImageBitmap(bitMapImg)
        }

        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(position,results[position])
            }
        }

    }
    class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
        val userNameText : TextView = view.findViewById(R.id.userNameText)
        val userChatText : TextView = view.findViewById(R.id.userChatText)
        val chatImageView : CircleImageView = view.findViewById(R.id.chat_image)


    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, item: Chat)
    }

    private fun decodeImage(image : String) : Bitmap {
        val imageBytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
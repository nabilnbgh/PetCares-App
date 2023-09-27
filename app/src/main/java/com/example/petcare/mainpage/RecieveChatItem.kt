package com.example.petcare.mainpage

import android.view.View
import com.example.petcare.R
import com.example.petcare.databinding.ItemChatRecieveBinding
import com.example.petcare.model.Message
import com.xwray.groupie.viewbinding.BindableItem

class RecieveChatItem(val message : Message) : BindableItem<ItemChatRecieveBinding>() {
    override fun bind(viewBinding: ItemChatRecieveBinding, position: Int) {
        viewBinding.message = message
    }

    override fun getLayout(): Int = R.layout.item_chat_recieve

    override fun initializeViewBinding(view: View): ItemChatRecieveBinding = ItemChatRecieveBinding.bind(view)

}
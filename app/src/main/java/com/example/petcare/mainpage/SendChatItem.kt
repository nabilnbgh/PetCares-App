package com.example.petcare.mainpage

import android.view.View
import com.example.petcare.R
import com.example.petcare.databinding.ItemChatSendBinding
import com.example.petcare.model.Message
import com.xwray.groupie.viewbinding.BindableItem

class SendChatItem(val message : Message) : BindableItem<ItemChatSendBinding>() {
    override fun bind(viewBinding: ItemChatSendBinding, position: Int) {
        viewBinding.message = message
    }
    override fun getLayout(): Int = R.layout.item_chat_send

    override fun initializeViewBinding(view: View): ItemChatSendBinding = ItemChatSendBinding.bind(view)

}

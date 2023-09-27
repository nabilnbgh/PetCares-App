package com.example.petcare.petprofile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import de.hdodenhof.circleimageview.CircleImageView

class AvatarAdapter(var results : ArrayList<Int>) : RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_avatar_item, parent, false)
    )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.petImage.setImageResource(results[position])
        if(selectedPosition == position){
            holder.petImage.borderColor = ContextCompat.getColor(holder.view.context,R.color.primaryColor)
            holder.petImage.borderWidth = 8
        }else{
            holder.petImage.borderColor = ContextCompat.getColor(holder.view.context,R.color.white)
            holder.petImage.borderWidth = 0
        }
        holder.itemView.setOnClickListener{
            selectedPosition = holder.bindingAdapterPosition
            notifyDataSetChanged()
        }
    }

    fun getSelectedPosition() : Int{
        return selectedPosition
    }




    class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
        val petImage : CircleImageView = view.findViewById(R.id.petProfileImage)

    }

}
package com.example.petcare.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.Pet
import de.hdodenhof.circleimageview.CircleImageView

class PetProfileAdapter(var results : ArrayList<Pet>) : RecyclerView.Adapter<PetProfileAdapter.ViewHolder>() {
    private var avatarList = setAvatarList()
    private var onItemClickListener: PetProfileAdapter.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_pet_profile_item, parent, false)
    )
    override fun getItemCount(): Int = results.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val index = results[position].foto_hewan
        if(index != -1){
            holder.petImage.setImageResource(avatarList.get(index))
        }else{
            holder.petImage.setImageResource(R.drawable.no_image)
        }
        holder.petName.text = results[position].nama_hewan
        holder.itemView.setOnClickListener{
            onItemClickListener?.onItemClick(position,results[position])
            true
        }
    }

    class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
        val petImage : CircleImageView = view.findViewById(R.id.petProfileAvatarImage)
        val petName : TextView = view.findViewById(R.id.petProfileNameText)

    }

    fun updateRV(newPetList : ArrayList<Pet>){
        results = newPetList
        notifyDataSetChanged()
    }

    private fun setAvatarList() : ArrayList<Int>{
        val tempArray = ArrayList<Int>()
        tempArray.add(R.drawable.avatar_cat_1)
        tempArray.add(R.drawable.avatar_cat_2)
        tempArray.add(R.drawable.avatar_cat_3)
        tempArray.add(R.drawable.avatar_cat_4)
        tempArray.add(R.drawable.avatar_cat_5)
        tempArray.add(R.drawable.avatar_cat_6)
        tempArray.add(R.drawable.avatar_cat_7)
        tempArray.add(R.drawable.avatar_cat_8)
        tempArray.add(R.drawable.avatar_cat_9)
        tempArray.add(R.drawable.avatar_dog_1)
        tempArray.add(R.drawable.avatar_dog_2)
        tempArray.add(R.drawable.avatar_dog_3)
        tempArray.add(R.drawable.avatar_dog_4)
        return tempArray

    }

    fun setOnItemClickListener(onItemClickListener: PetProfileAdapter.OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int, item : Pet)
    }
}

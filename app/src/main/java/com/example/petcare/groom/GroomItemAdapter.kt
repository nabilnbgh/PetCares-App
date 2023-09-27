package com.example.petcare.groom

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.GroomingItem

class GroomItemAdapter(var results: ArrayList<GroomingItem>) : RecyclerView.Adapter<GroomItemAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_grooming, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        if(result.foto_lokasi != ""){
            val bitmapImg = decodeImage(result.foto_lokasi)
            holder.placePhoto.setImageBitmap(bitmapImg)
        }
        holder.placeName.text = result.nama_lokasi
        holder.placeRating.text = result.rating.toString()
        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(position, result)
            }
        }
        val distance = results[position].distance
        holder.placeDistance.text = if(distance !=null){
            "%.2f".format(distance) + " KM"
        }else{
            "Unknown KM"
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, item: GroomingItem)
    }

    class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
        val placeName   : TextView = view.findViewById(R.id.placeName)
        val placeRating : TextView = view.findViewById(R.id.placeRating)
        val placeDistance : TextView = view.findViewById(R.id.placeDistance)
        val placePhoto : ImageView = view.findViewById(R.id.placePhoto)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(filteredList : ArrayList<GroomingItem>){
        results = filteredList
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setRVmWithDistance(newGroomingList : ArrayList<GroomingItem>){
        results = newGroomingList
        notifyDataSetChanged()
    }

    private fun decodeImage(image : String) : Bitmap {
        val imageBytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
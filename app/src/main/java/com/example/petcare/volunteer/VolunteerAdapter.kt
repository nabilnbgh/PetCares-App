package com.example.petcare.volunteer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.VolunteerLocation
class VolunteerAdapter(val results : List<VolunteerLocation>) : RecyclerView.Adapter<VolunteerAdapter.ViewHolder>(){
    private var onClickListener : VolunteerAdapter.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_grooming, parent, false)
        )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = results[position].nama_tempat_voluntir
        holder.rating.text = results[position].rating.toString()
        val distance = results[position].distance
        if(results[position].id_foto != -1){
            holder.typeImage.setImageResource(results[position].id_foto)
        }
        holder.jarak.text = if(distance !=null){
            "%.2f".format(distance) + " KM"
        }else{
            "Unknown KM"
        }
        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(position,results[position])
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)  {
        var name  : TextView = view.findViewById(R.id.placeName)
        var typeImage      : ImageView = view.findViewById(R.id.placePhoto)
        var rating  : TextView = view.findViewById(R.id.placeRating)
        var jarak : TextView = view.findViewById(R.id.placeDistance)
    }

    fun setOnClickListener(onClickListener: VolunteerAdapter.OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, item: VolunteerLocation)
    }
}
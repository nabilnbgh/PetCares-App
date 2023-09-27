package com.example.petcare.vaccinedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.Harga

class VaccinePriceAdapter(val results : ArrayList<Harga>) : RecyclerView.Adapter<VaccinePriceAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_price_item, parent, false)
    )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.deskripsi.text = results[position].deskripsi
        holder.detailDeskripsi.text = results[position].deskripsi
        holder.harga.text = results[position].harga
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var deskripsi  : TextView = view.findViewById(R.id.deskripsiText)
        var harga      : TextView = view.findViewById(R.id.hargaText)
        var detailDeskripsi  : TextView = view.findViewById(R.id.detailDeskripsi)
    }
}
package com.example.petcare.vaccinedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.Review

class VaccineReviewAdapter(private val results : ArrayList<Review>) : RecyclerView.Adapter<VaccineReviewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_reviewgrooming, parent, false)
    )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userReview.text = results[position].review_text.toString()
        holder.userName.text = results[position].nama_reviewer
        val rating = "%.2f".format(results[position].rating)
        holder.userRating.text = rating
    }

    class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
        val userRating : TextView = view.findViewById(R.id.userRating)
        val userName : TextView = view.findViewById(R.id.userName)
        val userReview : TextView = view.findViewById(R.id.userReview)

    }


}

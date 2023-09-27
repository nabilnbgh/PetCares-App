package com.example.petcare.mainpage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.model.News

class NewsAdapter(val context : Context, val results : ArrayList<News>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private var onClickListener : NewsAdapter.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_news, parent, false)
    )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val temp = when(results[position].foto_berita){
            1 -> R.drawable.news_image_2
            2 -> R.drawable.news_image_3
            else -> R.drawable.news_image_1
        }
        val img = ContextCompat.getDrawable(context, temp)
        holder.newsImageView.setImageDrawable(img)
        holder.newsTitleTextView.text = results[position].judul_berita
        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(position, results[position])
            }
        }


    }
    class ViewHolder (val view: View) : RecyclerView.ViewHolder(view) {
        val newsTitleTextView : TextView = view.findViewById(R.id.newsTitleText)
        val newsImageView : ImageView = view.findViewById(R.id.newsImage)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, item: News)
    }

}
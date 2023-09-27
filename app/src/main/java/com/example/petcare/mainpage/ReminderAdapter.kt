package com.example.petcare.mainpage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcare.R
import com.example.petcare.room.Reminder

class ReminderAdapter(val results : ArrayList<Reminder>) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>(){
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_calendar_item, parent, false)
    )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(results[position].pet_name != "" ){
            val name = results[position].pet_name
            holder.petText.text = "Pengingat untuk " + name
        }else{
            holder.petText.text = "Pengingat"
        }

        holder.reminderText.text = results[position].reminder_text
        var newHour = results[position].hour
        val timeSet: String
        if (newHour > 12) {
            newHour -= 12
            timeSet = "PM"
        } else if (newHour == 0) {
            newHour += 12
            timeSet = "AM"
        } else if (newHour == 12) {
            timeSet = "PM"
        } else {
            timeSet = "AM"
        }
        val min: String
        val minutes = results[position].minute
        if (minutes< 10) min = "0$minutes" else min = minutes.toString()
        val newTime = ""+ newHour + ":" + min + " " + timeSet
        holder.timeText.text = newTime

        holder.itemView.setOnLongClickListener{
            onItemClickListener?.onItemLongClick(position,results[position])
            true
        }

        holder.itemView.setOnClickListener{
            onItemClickListener?.onClickListener(position,results[position])
        }
    }

    class ViewHolder(val view : View): RecyclerView.ViewHolder(view) {
        val petText : TextView = view.findViewById(R.id.petNameText)
        val reminderText : TextView = view.findViewById(R.id.calendarDetailText)
        val timeText : TextView = view.findViewById(R.id.timeText)
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemLongClick(position: Int, item : Reminder)

        fun onClickListener(position: Int, item : Reminder)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData (data : ArrayList<Reminder>){
        results.clear()
        results.addAll(data)
        notifyDataSetChanged()
    }

}
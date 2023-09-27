package com.example.petcare.service.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.petcare.R

class ReminderBroadcast : BroadcastReceiver() {


    @SuppressLint("MissingPermission")
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p0 != null){
            val title: String = p1?.getStringExtra("title") ?: "Reminder"
            val content: String = p1?.getStringExtra("content") ?: ""
            val requestCode = p1?.getIntExtra("requestCode",-1)!!
            val vnpath : String? = p1.getStringExtra("vnpath")
            val id = p0.getString(R.string.channel_id_reminder)
            val builder = NotificationCompat.Builder(p0, id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(p0)
            notificationManager.notify("reminder",requestCode,builder.build())
            if (vnpath != null) {
                val mediaPlayer = MediaPlayer.create(p0, Uri.parse(vnpath))
                mediaPlayer.start()
            }

        }

    }
}
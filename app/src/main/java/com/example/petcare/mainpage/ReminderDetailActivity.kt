package com.example.petcare.mainpage

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.petcare.R
import com.example.petcare.databinding.ActivityReminderDetailBinding
import com.example.petcare.room.Reminder
import com.example.petcare.room.UserReminderDatabase
import com.example.petcare.service.broadcast.ReminderBroadcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ReminderDetailActivity : AppCompatActivity() {
    private lateinit var reminderItem: Reminder
    private lateinit var binding : ActivityReminderDetailBinding
    private val database by lazy { UserReminderDatabase(this) }
    private var audioFilePath: String? = null
    private lateinit var file : File
    private var player: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderDetailBinding.inflate(layoutInflater)
        reminderItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("reminder_item", Reminder::class.java)
        } else {
            intent.getParcelableExtra("reminder_item")
        } as Reminder

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            val path = Environment.getExternalStorageDirectory().absolutePath + "/PetCare"
            audioFilePath = "${path}/${reminderItem.id}.3gp"
        }else{
            val musicDirectory = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            audioFilePath = "$musicDirectory/${reminderItem.id}.3gp"
        }
        file = File(audioFilePath)
        //kalau exist, play button
        if(file.exists()){
            binding.playVoiceButton.visibility = View.VISIBLE
            binding.playVoiceButton.setOnClickListener {
                if (player == null) {
                    startPlaying()
                } else {
                    stopPlaying()
                }
            }
        }

        binding.reminderDescText.text = reminderItem.reminder_text
        setDateDailogText(reminderItem.year,reminderItem.month,reminderItem.day)
        setTimeDialogText(reminderItem.hour,reminderItem.minute)
        setContentView(binding.root)
        binding.petNameText.text =  reminderItem.pet_name
        binding.reminderDeleteButton.setOnClickListener {
            showDeleteDialog(reminderItem)
        }
    }
    private fun startPlaying() {
        val getDrawable = getDrawable(R.drawable.baseline_stop_24)
        binding.playVoiceButton.icon = getDrawable
        binding.playVoiceButton.text = "Berhenti Dengar"
        player = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath)
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setOnCompletionListener {
                stopPlaying()
            }
        }
    }
    private fun stopPlaying() {
        val getDrawable = getDrawable(R.drawable.baseline_play_arrow_24)
        binding.playVoiceButton.icon = getDrawable
        binding.playVoiceButton.text = "Dengar Suara"
        player?.apply {
            stop()
            release()
        }
        player = null
    }
    private fun setTimeDialogText(selectedHour : Int, selectedMin: Int){
        var newHour = selectedHour
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
        val min: String = if (selectedMin < 10) "0$selectedMin" else selectedMin.toString()
        val newTime = ""+ newHour + ":" + min + " " + timeSet

        //binding text and reminder obj
        binding.timeText.text = newTime
    }
    private fun setDateDailogText(selectedYear: Int,selectedMonth : Int ,selectedDay : Int){
        val newDay: String = if(selectedDay < 10) "0$selectedDay" else selectedDay.toString()
        val newMonth: String
        val tempMonth = selectedMonth + 1
        newMonth = if(tempMonth < 10) "0$tempMonth" else tempMonth.toString()
        val newDate = "" + newDay + "/" + newMonth + "/" + selectedYear.toString()
        binding.dateText.text = newDate
    }


    private fun showDeleteDialog(item : Reminder) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Hapus Notifikasi")
            .setMessage("Apakah Anda ingin menghapus notifikasi ini?")
            .setPositiveButton("Delete") { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton("Cancel", null)
            .create()
        alertDialog.show()
    }

    private fun deleteItem(item: Reminder){
        CoroutineScope(Dispatchers.IO).launch {
            database.userReminderDao().deleteUserReminder(item)
            deleteNotification(item)
        }
        CoroutineScope(Dispatchers.Main).launch {
            finish()
        }

    }

    private fun deleteNotification(item : Reminder){
        val intent = Intent(this, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            item.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        if(notificationManager != null){
            notificationManager.cancel("reminder",item.id)
        }

        //delete voice note file
        try{
            file.delete()
        }catch (e : SecurityException){
            Log.e("DeleteNotification", "Failed to delete voice note file", e)
        }
    }
}
package com.example.petcare.mainpage

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.petcare.R
import com.example.petcare.databinding.ActivityPickTimeBinding
import com.example.petcare.model.Pet
import com.example.petcare.room.Reminder
import com.example.petcare.room.UserReminderDatabase
import com.example.petcare.service.APICall.APIService
import com.example.petcare.service.broadcast.ReminderBroadcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class PickTimeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPickTimeBinding
    private lateinit var finalReminder : Reminder
    private var timeInMills : Long = 0
    private var requestCode = -1
    private var defaultRequestCode = 0
    private var petList = ArrayList<String>()
    private lateinit var adapter : ArrayAdapter<String>
    private val database by lazy { UserReminderDatabase(this) }
    private lateinit var preferences : SharedPreferences
    private val PERMISSIONS_REQUEST_AUDIO = 200
    private var isRecording = false
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var path : String? = null
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialize all variable
        preferences = getSharedPreferences("userData", MODE_PRIVATE)
        binding = ActivityPickTimeBinding.inflate(layoutInflater)
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, petList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setSpinner()
        //[Initialize Value Section]
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            path = Environment.getExternalStorageDirectory().absolutePath + "/PetCare"
            val file = File(path)
            if (!file.exists() || !file.isDirectory) {
                file.mkdirs()
            }
            audioFilePath = "${path}/temp.3gp"
        }else{
            var musicDirectory = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            //mkdir if not exist
            if (musicDirectory == null) {
                musicDirectory = File(getExternalFilesDir(null), Environment.DIRECTORY_MUSIC)
                musicDirectory.mkdirs()
            }
            path = "$musicDirectory"
            audioFilePath = "$path/temp.3gp"
        }
        val date = Calendar.getInstance()
        timeInMills = System.currentTimeMillis()
        //get time & date
        finalReminder = getFinalReminder(date)
        setTimeDateText(date.time)
        setTimeDateDialog(date)



        //[Binding UI Section]
        binding.reminderExitButton.setOnClickListener {
            deleteRecording()
            audioFilePath = null
            finish()
        }
        binding.reminderSaveButton.setOnClickListener {
            //check if desc edit text is empty
            if(binding.reminderDescText.text.toString() != "" || audioFilePath !=null ){
                //send reminder to BE
                sendReminder()
            }
        }
        binding.recordVoiceButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    showAlertPermission()
                }
                else{
                    requestAudioStoragePermission()
                }
            }else{
                if (isRecording) {
                    stopRecording()
                } else {
                    startRecording()
                }
            }

        }
        binding.playVoiceButton.setOnClickListener{
            if (player == null) {
                startPlaying()
            } else {
                stopPlaying()
            }
        }

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        deleteRecording()
        audioFilePath = null

    }

    //[Voice Note Section]
    private fun deleteRecording() {
        val file = File(audioFilePath)
        if (file.exists()) {
            file.delete()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        binding.recordVoiceButton.text = "Berhenti Rekam"
        binding.reminderSaveButton.isEnabled = false
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
                start()
                isRecording = true
                Toast.makeText(this@PickTimeActivity, "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }

    private fun stopRecording(){
        binding.reminderSaveButton.isEnabled = true
        binding.recordVoiceButton.text = "Rekam Suara"
        recorder?.apply {
            stop()
            release()
            isRecording = false
        }
        recorder = null
        binding.playVoiceButton.visibility = View.VISIBLE
        Toast.makeText(this@PickTimeActivity, "Recording stopped", Toast.LENGTH_SHORT).show()
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
    private fun showAlertPermission(){
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setMessage("Aplikasi membutuhkan akses untuk merekam suaramu")
            .setTitle("Permintaan Izin")
            .setCancelable(false)
            .setPositiveButton("OK", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    requestAudioStoragePermission()
                }
            }).create().show()
    }
    private fun requestAudioStoragePermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),PERMISSIONS_REQUEST_AUDIO
        )
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSIONS_REQUEST_AUDIO -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    if (isRecording) {
                        stopRecording()
                    } else {
                        startRecording()
                    }
                }else{
                    Toast.makeText(this,"Tidak ada izin rekam suara/audio, tolong ubah izin rekam suara/audio",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    //[Pet Profile Section]
    private fun setSpinner(){
        binding.petSpinner.setAdapter(adapter)

        getNewSpinner()
    }
    private fun getNewSpinner(){
        val id = preferences.getString("id", "null").toString()
        APIService.endpoint.getAllPetProfile(id).enqueue(object :Callback<ArrayList<Pet>>{
            override fun onResponse(
                call: Call<ArrayList<Pet>>,
                response: Response<ArrayList<Pet>>
            ) {
                if(response.isSuccessful){
                    notifyChangeSpinner(response.body()!!)
                }
            }

            override fun onFailure(call: Call<ArrayList<Pet>>, t: Throwable) {

            }

        })
    }
    private fun notifyChangeSpinner(pet : ArrayList<Pet>){
        pet.forEach {
            petList.add(it.nama_hewan)
        }
        adapter.notifyDataSetChanged()

    }
    @SuppressLint("SimpleDateFormat")

    //[Notification Section]
    private fun setTimeDateText(date : Date){
        val df = SimpleDateFormat("dd/MM/yyyy")
        val tf = SimpleDateFormat("h:mm a")
        val day = df.format(date)
        val time = tf.format(date)
        binding.dateText.text = day
        binding.timeText.text = time
    }
    private fun setTimeDateDialog(date : Calendar){
        binding.timeText.setOnClickListener {
            val hour = date[Calendar.HOUR_OF_DAY]
            val minute = date[Calendar.MINUTE]
            val timePicker = TimePickerDialog(this, {_, selectedHour, selectedMin ->
                setTimeDialogText(selectedHour,selectedMin)
                finalReminder.hour = selectedHour
                finalReminder.minute = selectedMin
            },hour,minute, false)
            timePicker.setTitle("Select Time")
            timePicker.show()
        }
        binding.dateText.setOnClickListener {
            val day = date[Calendar.DAY_OF_MONTH]
            val month = date[Calendar.MONTH]
            val year = date[Calendar.YEAR]
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                setDateDailogText(selectedYear,selectedMonth,selectedDay)
                finalReminder.day = selectedDay
                finalReminder.month = selectedMonth
                finalReminder.year = selectedYear
            },year,month,day)
            datePicker.setTitle("Select Date")
            datePicker.show()
        }
    }
    private fun setTimeMillis(year: Int, month : Int,day : Int,hour :Int,minute: Int) : Calendar{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMills
        calendar.set(Calendar.YEAR,year)
        calendar.set(Calendar.MONTH,month)
        calendar.set(Calendar.DAY_OF_MONTH,day)
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)
        return calendar
    }

    //return final reminder + current date/time
    private fun getFinalReminder(date :Calendar) : Reminder{
        val hour = date[Calendar.HOUR_OF_DAY]
        val minute = date[Calendar.MINUTE]
        val day = date[Calendar.DAY_OF_MONTH]
        val month = date[Calendar.MONTH]
        val year = date[Calendar.YEAR]
        return Reminder(defaultRequestCode,"","",year,month,day,hour,minute)
    }
    private fun sendReminder(){
        if(binding.petSpinner.text.toString() != "Pilih Profil Hewan Peliharaan"){
            finalReminder.pet_name = binding.petSpinner.text.toString()
        }

        finalReminder.reminder_text = binding.reminderDescText.text.toString()
        updateDataToDB(finalReminder)

        this@PickTimeActivity.finish()

    }
    private fun setReminderNotification(){
        val file = File(audioFilePath)
        val intent = Intent(this@PickTimeActivity, ReminderBroadcast::class.java).apply {
//            putExtra("title", binding.petSpinner.selectedItem.toString())
            var textContent = binding.reminderDescText.text.toString()
            if(textContent == ""){
                if(binding.petSpinner.text.toString() != "Pilih Profil Hewan Peliharaan"){
                    textContent = "Pengingat untuk " + binding.petSpinner.text.toString()
                }
                else{
                    textContent = "Pengingat"
                }
            }
            putExtra("content", textContent)
            putExtra("requestCode",requestCode)
            if(file.exists()){
                putExtra("vnpath", audioFilePath)
            }
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this@PickTimeActivity,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = setTimeMillis(finalReminder.year,finalReminder.month,finalReminder.day,finalReminder.hour,finalReminder.minute)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,pendingIntent)
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
    private fun updateDataToDB(dataReminder: Reminder) = CoroutineScope(
        Dispatchers.IO).launch {
            try{
                requestCode = database.userReminderDao().addUserReminder(dataReminder).toInt()
                val file = File(audioFilePath)
                audioFilePath = "$path/$requestCode.3gp"
                val storedFile = File(audioFilePath)
                if(file.exists()){
                    file.renameTo(storedFile)
                }
                setReminderNotification()
            }
            catch (e:Exception){
                Log.e("Exception updateDataToDB",e.message.toString())
            }
        }





}
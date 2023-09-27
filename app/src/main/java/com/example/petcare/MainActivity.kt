package com.example.petcare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.petcare.Fragments.IntroViewPagerAdapter
import com.example.petcare.databinding.ActivityMainBinding
import com.example.petcare.entry.EntryActivity


class MainActivity : AppCompatActivity() {
    lateinit var preferences: SharedPreferences
    private val pref_show_intro = "Intro"
    private val userPref = "userPreference"
    private lateinit var binding: ActivityMainBinding
    private val viewPagerListener = object : ViewPager.OnPageChangeListener{
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            if(position == 2){
                binding.skipButton.visibility = View.INVISIBLE
                binding.entryButton.visibility = View.VISIBLE
            }else{
                binding.skipButton.visibility = View.VISIBLE
                binding.entryButton.visibility = View.INVISIBLE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        preferences = getSharedPreferences("introSlider", MODE_PRIVATE)
        setNotificationChannel()
        if(!preferences.getBoolean(pref_show_intro, true)) {
            preferences = getSharedPreferences(userPref, MODE_PRIVATE)
            if(preferences.getBoolean(userPref,true)){
                startActivity(Intent(this, EntryActivity::class.java))
            }else{
                startActivity(Intent(this, HomepageActivity::class.java))
            }

            finish()
        }
        binding.skipButton.setOnClickListener {
            binding.viewPager.setCurrentItem(2)
        }
        binding.viewPager.adapter = IntroViewPagerAdapter(supportFragmentManager)

        binding.viewPager.addOnPageChangeListener(viewPagerListener)

        binding.entryButton.setOnClickListener {
            val intent = Intent(this, EntryActivity::class.java)
            startActivity(intent)
            val editor = preferences.edit()
            editor.putBoolean(pref_show_intro, false)
            editor.apply()
            finish()
        }

        setContentView(view)
    }



    private fun setNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //set reminder channel
            val id = getString(R.string.channel_id_reminder)
            val name = getString(R.string.channel_name_reminder)
            val descriptionText = getString(R.string.channel_description_reminder)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
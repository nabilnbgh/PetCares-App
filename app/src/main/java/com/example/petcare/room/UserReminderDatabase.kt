package com.example.petcare.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class UserReminderDatabase : RoomDatabase(){

    abstract fun userReminderDao() : UserReminderDao

    companion object{
        @Volatile
        private var instance : UserReminderDatabase? = null
        private val LOCK = Any()


        //if there is exist db return instance, otherwise create db
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            UserReminderDatabase::class.java,
            "user_reminder_database"
        ).build()




    }

}
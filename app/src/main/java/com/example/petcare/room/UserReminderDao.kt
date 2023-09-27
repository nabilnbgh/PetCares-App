package com.example.petcare.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserReminderDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUserReminder(userReminder: Reminder) : Long

    @Query("SELECT * FROM user_reminder_table ORDER BY id ASC" )
    fun readAllUserReminder() : List<Reminder>

    @Query("SELECT * FROM user_reminder_table WHERE year = :selectedYear AND month = :selectedMonth AND day = :selectedDay" )
    fun readUserReminder(selectedYear: Int, selectedMonth: Int, selectedDay: Int) : List<Reminder>

    @Update
    suspend fun updateUserReminder(reminder: Reminder)

    @Delete
    suspend fun deleteUserReminder(reminder: Reminder)

    //be careful with this! delete room entity
    @Query("DELETE FROM user_reminder_table")
    fun nukeTable()


//    @Query("SELECT * FROM bookmarks WHERE faskes_id = (:faskesId)")
//    suspend fun getBookmarkByFaskesId(faskesId: Int): Bookmark

}

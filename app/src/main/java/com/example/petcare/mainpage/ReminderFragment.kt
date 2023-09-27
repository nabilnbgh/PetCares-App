package com.example.petcare.mainpage

import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.R
import com.example.petcare.databinding.FragmentReminderBinding
import com.example.petcare.room.Reminder
import com.example.petcare.room.UserReminderDatabase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ReminderFragment : Fragment() {

    private var _binding : FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private var reminderList = ArrayList<Reminder>()
    private val database by lazy { UserReminderDatabase(requireActivity()) }
    private var selectedYear : Int = 0
    private var selectedMonth : Int = 0
    private var selectedDayOfMonth : Int = 0
    private lateinit var currentDate : CalendarDay
    private var allReminderList = ArrayList<Reminder>()
    private var span = DotSpan(5.0f,R.color.primaryColor)
    private lateinit var reminderAdapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReminderBinding.inflate(inflater,container,false)
        setReminderRV()
        setCurrentDateOnCalendar()
        getReminderFromDB {setSpin(allReminderList) }
        binding.calendarFAB.setOnClickListener {
            startActivity(Intent(requireContext(), PickTimeActivity::class.java))
        }

        getActiveNotification()

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            currentDate = date
            selectedYear = date.year
            selectedMonth = date.month-1
            selectedDayOfMonth = date.day
            getReminderFromDB {}
        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.calendarView.removeDecorators()
        getReminderFromDB {
            setSpin(allReminderList)
        }
    }

    private fun setCurrentDateOnCalendar(){
        val today = CalendarDay.today()
        selectedDayOfMonth = today.day
        selectedMonth = today.month-1
        selectedYear = today.year
        binding.calendarView.selectedDate = today
    }
    private fun setReminderRV(){
        reminderAdapter = ReminderAdapter(reminderList)
        reminderAdapter.setOnItemClickListener(object : ReminderAdapter.OnItemClickListener {
            override fun onItemLongClick(position: Int, item: Reminder) {
            }

            override fun onClickListener(position: Int, item: Reminder) {
                showReminderDetail(item)
            }

        })
        val calRV = binding.calendarRV
        calRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reminderAdapter
        }
    }

    private fun showReminderDetail(item: Reminder){
        val intent = Intent(requireContext(),ReminderDetailActivity::class.java)
        intent.putExtra("reminder_item",item)
        startActivity(intent)
    }
    private fun getReminderFromDB(callback: (Boolean) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val reminderList = database.userReminderDao().readUserReminder(selectedYear,selectedMonth,selectedDayOfMonth) as ArrayList<Reminder>
            allReminderList = database.userReminderDao().readAllUserReminder() as ArrayList<Reminder>
            CoroutineScope(Dispatchers.Main). launch {
                Log.d("reminder list dari DB", reminderList.toString())
                setRVWithNewData(reminderList)
                callback(true)
            }
        }
    }


    private fun setSpin(allReminderList : ArrayList<Reminder>){
        if(allReminderList.isNotEmpty()){
            Log.d("list not empty", "SetSpin: true")
            Log.d("all reminder", allReminderList.toString())
            allReminderList.forEach {
                val selectedYear = it.year
                val selectedMonth = it.month
                val selectedDayOfMonth = it.day
                binding.calendarView.addDecorator(
                    EventDecorator(span, CalendarDay.from(selectedYear,selectedMonth+1,selectedDayOfMonth))
                )
            }
        }
    }
    private fun getActiveNotification(){
        val mNotificationManager =requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        val notifications = mNotificationManager!!.activeNotifications

        notifications.forEach {
            Log.d("Notification", it.toString())
        }
    }
    private fun setRVWithNewData(items: ArrayList<Reminder>){
            reminderAdapter.setData(items)
    }

    inner class EventDecorator(private val span: DotSpan, private val date: CalendarDay) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day == date
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(span)
        }
    }

}
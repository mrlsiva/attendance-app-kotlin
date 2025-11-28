package com.slings.vasantham.ui.CalendarLog

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.applandeo.materialcalendarview.utils.setSelectedDayColors
import com.slings.vasantham.AttendanceSuccessDetail
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.ViewModel.MainViewModel
import com.slings.vasantham.databinding.AnotherLinearLayoutBinding
import com.slings.vasantham.databinding.LogsBinding
import com.slings.vasantham.service.RetrofitService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.internal.notify
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


class CalendarLogFragment : Fragment() {

    private var _binding: LogsBinding? = null
    private val client = OkHttpClient()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var mCalendar: Calendar
    var list = ArrayList<CalendarDay>()
    private lateinit var calendarLegends: AnotherLinearLayoutBinding


    lateinit var viewModel: MainViewModel
    lateinit var sweetAlertDialog: SweetAlertDialog
    lateinit var retrofitService: RetrofitService

    lateinit var calendarView: CalendarView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        _binding = LogsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mCalendar = Calendar.getInstance()
        retrofitService = RetrofitService.getInstance()

        viewModel = ViewModelProvider(this@CalendarLogFragment)[MainViewModel::class.java]
        sweetAlertDialog = SweetAlertDialog(
            activity,
            SweetAlertDialog.PROGRESS_TYPE
        )
        sweetAlertDialog.setTitleText("Loading")
        if (!sweetAlertDialog.isShowing) {
            sweetAlertDialog.show()
        }

        createCalendar()

        lifecycleScope.launch {
            delay(1200)
            val currentDate = LocalDate.now()
            val currentMonthNumeric = currentDate.monthValue
            calendarLoad(mCalendar.get(Calendar.YEAR), currentMonthNumeric)
        }


        return root
    }

    fun isCurrentDate(calendar: Calendar): Boolean {
        val currentDate = Calendar.getInstance()

        return (calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH))
    }

    fun getScreenDensityPercentage(context: Context): Float {
        val displayMetrics = context.resources.displayMetrics
        val densityDpi = displayMetrics.densityDpi.toFloat()
        val referenceDpi = DisplayMetrics.DENSITY_DEFAULT.toFloat()

        // Calculate density percentage
        return (densityDpi / referenceDpi) * 100
    }


    fun createCalendar() {

        lifecycleScope.launch() {
            delay(1000)
            calendarView = CalendarView(requireActivity())
            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (getScreenDensityPercentage(requireContext())*4.5).toInt())
            calendarView.layoutParams = layoutParams

            binding.calendarview.addView(calendarView)
//            val displayMetrics = requireContext().resources.displayMetrics
//            val params = calendarView.layoutParams as LinearLayout.LayoutParams
//            params.height += (180 / (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
//            calendarView.layoutParams = params

            val inflater = LayoutInflater.from(activity)
            calendarLegends = AnotherLinearLayoutBinding.inflate(
                inflater, binding.calendarview, false
            )

            val calendarView1 = calendarLegends.calendarviewLegends
            val layoutParams1 = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            calendarView1.layoutParams = layoutParams1
            binding.calendarview.addView(calendarView1)
            calendarView.setHeaderColor(R.color.dash_back_color)
            calendarView.setSelectionBackground(com.applandeo.materialcalendarview.R.drawable.background_color_circle_selector)
            calendarView.setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    val clickedDayCalendar: Calendar = eventDay.calendar
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formattedDate = clickedDayCalendar.toInstant()
                        .atZone(clickedDayCalendar.timeZone.toZoneId()).toLocalDate()
                        .format(formatter)
                    val currentCalendar: Calendar = Calendar.getInstance()

                    if (clickedDayCalendar.compareTo(currentCalendar) > 0) {
                        return
                    }

                    if (CalendarDay(clickedDayCalendar).attendanceStatus == 3) {
                        val pDialog2 =
                            SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                        pDialog2.titleText = "Leave Applied for selected date"
                        pDialog2.setCancelable(true)
                        pDialog2.setConfirmText("OK")
                            .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener { sweetAlertDialog ->
                                sweetAlertDialog.dismiss()
                            })
                        pDialog2.show()
                    } else {
                        val intent = Intent(activity, AttendanceSuccessDetail::class.java)
                        intent.putExtra("date", formattedDate)
                        startActivity(intent)
                    }
                }
            })
            calendarView.setOnPreviousPageChangeListener(object :
                OnCalendarPageChangeListener {
                override fun onChange() {
                    mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) - 1)
                    val currentYear = mCalendar.get(Calendar.YEAR)
                    val currentMonth = mCalendar.get(Calendar.MONTH) + 1
                    if (!sweetAlertDialog.isShowing) {
                        sweetAlertDialog.show()
                    }
                    Thread {
                        activity!!.runOnUiThread {
                            calendarView.clearSelectedDays()
                            calendarView.invalidate()
                        }
                    }
                    calendarLoad(currentYear, currentMonth)
                }
            })
            calendarView.setOnForwardPageChangeListener(object :
                OnCalendarPageChangeListener {
                override fun onChange() {
                    mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) + 1)
                    val currentYear = mCalendar.get(Calendar.YEAR)
                    val currentMonth = mCalendar.get(Calendar.MONTH) + 1
                    if (!sweetAlertDialog.isShowing) {
                        sweetAlertDialog.show()
                    }
                    Thread {
                        activity!!.runOnUiThread {
                            calendarView.clearSelectedDays()
                            calendarView.invalidate()
                        }
                    }
                    lifecycleScope.launch {
                        calendarLoad(currentYear, currentMonth)
                    }
                }
            })

        }

    }

    fun calendarLoad(year: Int, month: Int) {
        if (!sweetAlertDialog.isShowing) {
            sweetAlertDialog.show()
        }
        lifecycleScope.launch {
            delay(500)

            viewModel.getAttMonthLogLiveData().observe(viewLifecycleOwner, Observer {
                try {
                    val daysOfMonth = ArrayList<String>()
                    try {
                        calendarLegends.leaveValue.text = "Leave (" + it.data.leaves.size + ")"
                        calendarLegends.presentValue.text =
                            "Present (" + it.data.presents.size + ")"
                        calendarLegends.permissionValue.text =
                            "Permission (" + it.data.permissions.size + ")"
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    for (i in 0 until it.data.permissions.size) {
                        daysOfMonth.add(it.data.permissions[i].toString() + "-premission")
                    }
                    for (i in 0 until it.data.presents.size) {
                        daysOfMonth.add(it.data.presents[i].toString() + "-present")
                    }
                    for (i in 0 until it.data.leaves.size) {
                        daysOfMonth.add(it.data.leaves[i].toString() + "-leave")
                    }


                    val currentYear = mCalendar.get(Calendar.YEAR)
                    val currentMonth =
                        mCalendar.get(Calendar.MONTH) + 1 // Note: Months are zero-based in Calendar

                    Thread {
                        requireActivity().runOnUiThread {
                            calendarView.clearSelectedDays()
                            calendarView.invalidate()
                        }
                    }

                    list.clear()

                    // Populate the list with Calendar objects for each day of the month
                    for (dayOfMonth in daysOfMonth) {
                        mCalendar = Calendar.getInstance()
                        mCalendar.set(
                            currentYear,
                            currentMonth - 1,
                            Integer.parseInt(dayOfMonth.substring(0, dayOfMonth.indexOf("-")))
                        ) // Note: Months are zero-based in Calendar


                        if (dayOfMonth.contains("-premission")) {

                            list.add(CalendarDay(mCalendar).apply {
                                labelColor = R.color.white
                                backgroundResource =
                                    com.applandeo.materialcalendarview.R.drawable.background_color_circle_selector
                                selectedLabelColor = R.color.permissioncolor
                                selectedBackgroundResource =
                                    com.applandeo.materialcalendarview.R.drawable.background_color_circle_selector
                                attendanceStatus = 2
                            })
                        } else if (dayOfMonth.contains("-present")) {
                            list.add(CalendarDay(mCalendar).apply {
                                labelColor = R.color.white
                                backgroundResource =
                                    com.applandeo.materialcalendarview.R.drawable.background_circle_present
                                selectedLabelColor = R.color.permissioncolor
                                selectedBackgroundResource =
                                    com.applandeo.materialcalendarview.R.drawable.background_circle_present
                                attendanceStatus = 1
                            })
                        } else if (dayOfMonth.contains("-leave")) {
                            list.add(CalendarDay(mCalendar).apply {
                                labelColor = R.color.white
                                backgroundResource =
                                    com.applandeo.materialcalendarview.R.drawable.background_circle_leave
                                selectedLabelColor = R.color.permissioncolor
                                selectedBackgroundResource =
                                    com.applandeo.materialcalendarview.R.drawable.background_circle_leave
                                attendanceStatus = 3
                            })
                        }
                    }
                    calendarView.setCalendarDays(list)
                    calendarView.invalidate()
                    calendarView.notify()

                } catch (e: Exception) {

                }
                requireActivity().runOnUiThread{
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (sweetAlertDialog.isShowing) {
                            sweetAlertDialog.dismiss()
                        }

                    }, 2000)
                }
            })
            viewModel.attendanceMonthlyLog(
                Util.getPreference(requireActivity().applicationContext, "userId", "").toString(),
                year, month, retrofitService
            )
        }
    }

    private fun clearPreviousSelection(calendarView: CalendarView) {
        val selectedDays: List<Calendar> = calendarView.selectedDates
        for (selectedDay in selectedDays) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.slings.vasantham

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import java.text.ParseException


class AttendanceSucess : AppCompatActivity() {

    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attendance_success)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val goHome = findViewById<Button>(R.id.goHome)
        val success = findViewById<TextView>(R.id.success)


        try {

            success.text = "Thank you..! "+intent.getStringExtra("lateBy")


        } catch (e: ParseException) {
            e.printStackTrace()
        }

        goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /* fun getTimeDifferenceAgo(time1: String, time2: String): String {
         val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

         val time1Date = timeFormat.parse(time1)
         val time2Date = dateTimeFormat.parse(time2)

         val calendar1 = Calendar.getInstance()
         calendar1.time = time1Date

         val calendar2 = Calendar.getInstance()
         calendar2.time = time2Date

         val isTime2BeforeTime1 = calendar2.get(Calendar.HOUR_OF_DAY) < calendar1.get(Calendar.HOUR_OF_DAY) ||
                 (calendar2.get(Calendar.HOUR_OF_DAY) == calendar1.get(Calendar.HOUR_OF_DAY) &&
                         calendar2.get(Calendar.MINUTE) < calendar1.get(Calendar.MINUTE))

         if (isTime2BeforeTime1) {
             return "" // Return empty string if time2 is earlier than time1
         }

         val hoursDifference = calendar2.get(Calendar.HOUR_OF_DAY) - calendar1.get(Calendar.HOUR_OF_DAY)
         val minutesDifference = calendar2.get(Calendar.MINUTE) - calendar1.get(Calendar.MINUTE)

         return when {
             hoursDifference > 0 -> "$hoursDifference hrs $minutesDifference mins late"
             minutesDifference > 0 -> "$minutesDifference mins late"
             else -> "Just now"
         }
     }*/
}

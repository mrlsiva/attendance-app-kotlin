package com.slings.vasantham

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
//import okhttp3.OkHttpClient
import java.text.ParseException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AttendanceSucess : BaseActivity() {

//    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attendance_success)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val goHome = findViewById<Button>(R.id.goHome)
        val success = findViewById<TextView>(R.id.success)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Go Back"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        try {

            success.text = "Thank you..! "+intent.getStringExtra("lateBy")


        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = currentDate.format(formatter)
            val intent = Intent(this, AttendanceSuccessDetail::class.java)
            intent.putExtra("date",formattedDate)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

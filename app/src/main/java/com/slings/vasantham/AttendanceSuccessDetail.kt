package com.slings.vasantham

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.slings.vasantham.ViewModel.MainViewModel
import com.slings.vasantham.service.RetrofitService
import com.transferwise.sequencelayout.SequenceStep
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AttendanceSuccessDetail : BaseActivity() {

    private val client = OkHttpClient()
    lateinit var shiftTime: String
    lateinit var sweetAlertDialog: SweetAlertDialog
    lateinit var retrofitService:RetrofitService
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.success_atten_detail)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val attenName = findViewById<TextView>(R.id.atten_name)
        val attenDesgn = findViewById<TextView>(R.id.atten_desgn)
        val textViewCurrentTime = findViewById<TextView>(R.id.textViewCurrentTime)
        val attenLog = findViewById<AppCompatButton>(R.id.attenLog)
        val home = findViewById<AppCompatButton>(R.id.home)
       val permissionHours = findViewById<TextView>(R.id.permission_hours)
        var inTime = findViewById<SequenceStep>(R.id.inTime)
        val currentTime = findViewById<SequenceStep>(R.id.currentTime)
        val outTime = findViewById<SequenceStep>(R.id.outTime)

        setSupportActionBar(toolbar)
        toolbar.title = "Go Back"

        lifecycleScope.launch {
            delay(500)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true);
            supportActionBar!!.setDisplayShowHomeEnabled(true);
            var activeStatus = 0
            val shiftIn = findViewById<TextView>(R.id.punchToday)
            val shiftOut = findViewById<TextView>(R.id.punchToday2)
            try{
                shiftTime = Util.getPreference(
                    applicationContext, "shiftname", ""
                )!!
                attenName.text = "Hi, " + Util.getPreference(
                    applicationContext
                        .applicationContext, "username", ""
                )
                Glide.with(applicationContext)
                    .load(Util.getPreference(applicationContext,"profileUrl",""))
                    .error(com.slings.vasantham.R.drawable.menu_user_profile)
                    .into(findViewById<CircleImageView>(R.id.imageView))
                attenDesgn.text = "Welcome to Vasantham"
                val parser1 = SimpleDateFormat("yyyy-MM-dd")
                val formatter1 = SimpleDateFormat("dd-MM-yyyy")
                val output1 = formatter1.format(parser1.parse(intent.getStringExtra("date")))
                textViewCurrentTime.text = output1
                shiftIn.text = Util.getPreference(applicationContext, "shiftStart", "")!!
                shiftOut.text = Util.getPreference(applicationContext, "shiftEnd", "")!!
            }catch (e:Exception){
                e.printStackTrace()
            }

            attenLog.setOnClickListener {
                val intent = Intent(this@AttendanceSuccessDetail, MainActivity::class.java)
                intent.putExtra("openAttLog", true)
                startActivity(intent)
                finish()
            }
            home.setOnClickListener {
                val intent = Intent(this@AttendanceSuccessDetail, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            retrofitService = RetrofitService.getInstance()

            sweetAlertDialog = SweetAlertDialog(
                this@AttendanceSuccessDetail,
                SweetAlertDialog.PROGRESS_TYPE
            )
            sweetAlertDialog.setTitleText("Loading")
            viewModel = ViewModelProvider(this@AttendanceSuccessDetail)[MainViewModel::class.java]

            viewModel.attendanceDayLog(Util.getPreference(applicationContext,"userId","").toString()
                , intent.getStringExtra("date")!!,retrofitService)
            if(!sweetAlertDialog.isShowing){
                sweetAlertDialog.show()
            }

            viewModel.getAttenDayLogLiveData().observe(this@AttendanceSuccessDetail, Observer {

                if(sweetAlertDialog.isShowing){
                    sweetAlertDialog.dismiss()
                }
                try {
                    if (it.success) {
                        try {
//                            val parser = SimpleDateFormat("HH:mm:ss")
//                            val formatter = SimpleDateFormat("HH:mm a")
//                            val output = formatter.format(parser.parse())!!
                            inTime.setTitle(Util.getPreference(applicationContext,
                                "shiftStart",""))
                            activeStatus = 1
                            val parser1 = SimpleDateFormat("HH:mm:ss")
                            val formatter1 = SimpleDateFormat("HH:mm a")
                            val output1 = formatter1.format(parser1.parse(it.data.actualCheckInTime))
                            currentTime.setTitle(output1)
                            activeStatus = 2
                            val parser2 = SimpleDateFormat("HH:mm:ss")
                            val formatter2 = SimpleDateFormat("HH:mm a")
                            var output2 = ""
                            if (it.data.actualCheckOutTime!=null) {
                                try{
                                    output2 = formatter2.format(parser2.parse(it.data.actualCheckOutTime)!!)
                                    activeStatus = 3
                                }catch (e:Exception){
                                    output2 = Util.getPreference(applicationContext,
                                        "shiftEnd","").toString()
                                }
                            }else{
                                output2 =  Util.getPreference(applicationContext,
                                    "shiftEnd","").toString()
                            }

                            outTime.setTitle(output2)
                            when (activeStatus) {
                                1 -> {
                                    inTime.setActive(true)
                                }

                                2 -> {
                                    currentTime.setActive(true)
                                }

                                3 -> {
                                    outTime.setActive(true)
                                }
                            }

                            permissionHours.text = it.data.permissionInHours
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext
                        ,
                        "Network error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // Handle the back button press here
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

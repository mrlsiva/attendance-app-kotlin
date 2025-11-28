package com.slings.vasantham

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.slings.vasantham.BaseActivity.Companion.isActive
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class NoInternetActivity : AppCompatActivity() {

    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_internet)
        initView()
        checkConnection()
        NoInActivity = this
    }


    private fun initView() {
       val btn_retry = findViewById<TextView>(R.id.error_page_retry)
        btn_retry.setOnClickListener { checkConnection() }
        btn_retry.setPaintFlags(btn_retry.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

    }

    private fun checkConnection() {
        isConnected = MyReceiver.isConnected
        if (isConnected) {
            finish()
        } else {
//            parentLayout!!.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        checkConnection()
    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var NoInActivity: Activity? = null
    }
}
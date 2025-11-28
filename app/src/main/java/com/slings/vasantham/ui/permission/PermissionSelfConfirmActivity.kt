package com.slings.vasantham.ui.attendance

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.slings.vasantham.AttendanceSucess
import com.slings.vasantham.BaseActivity
import com.slings.vasantham.Common
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutionException

class PermissionSelfConfirmActivity : BaseActivity() {

    private lateinit var imageView: ImageView
    private val client = OkHttpClient()
    lateinit var loaderLayout: ConstraintLayout
    lateinit var destFile: File
    lateinit var retake: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.selfie_confirm)
        destFile = File(intent.getStringExtra("imageURL"))
        initUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun initUI() {
        imageView = findViewById(R.id.imageView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        loaderLayout = findViewById(R.id.loader_layout)
        setSupportActionBar(toolbar)
        toolbar.title = "Go Back"

        retake =  findViewById(R.id.retake)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        findViewById<Button>(R.id.upload).setOnClickListener {
            retake.visibility = View.GONE
            showProgressDialog()
        }

        retake.setOnClickListener {
            val intent = Intent(this, PermissionSelfConfirmActivity::class.java)
            startActivity(intent)
            finish()
        }

        Glide.with(this)
            .load(intent.getStringExtra("imageURL"))
            .into(imageView)
    }

    private fun showProgressDialog() {
        loaderLayout.visibility = View.VISIBLE
        startAttendanceIn()
    }

    suspend fun attendanceIn() {
        lateinit var response: Response
        val bufferSize = 4096
        val buffer = ByteArray(bufferSize)
        lateinit var json: JSONObject
        val imageFile = File(intent.getStringExtra("imageURL")!!)
        try {
            val mediaType: MediaType = "image/*".toMediaTypeOrNull()
                ?: throw IllegalArgumentException("Invalid media type")

            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reason", "")
                .addFormDataPart("imageUrl", destFile.name,
                    destFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url(
                    Common.URL +"staff/permission/"+
                            Util.getPreference(applicationContext,"userId","").toString())
                .post(requestBody)
                .build()
            withContext(Dispatchers.IO) {
                response = client.newCall(request).execute()
            }

            if (!response.isSuccessful) {
                throw IOException("HTTP Error: ${response.code}")
            }

            val responseBody = response.body?.string()
            json = JSONObject(responseBody!!)
            val data = json.getJSONObject("data")

            withContext(Dispatchers.Main) {
                val pDialog1 = SweetAlertDialog(this@PermissionSelfConfirmActivity,
                    SweetAlertDialog.NORMAL_TYPE)
                pDialog1.titleText = data.getString("message")
                pDialog1.setCancelable(true)
                pDialog1.setConfirmText("OK")
                    .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener { sweetAlertDialog ->
                        sweetAlertDialog.dismiss()
                        val navController = MainActivity.navController
                        navController.navigate(R.id.nav_permission)
                        finish()
                    })
                pDialog1.show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                if(json.getString("message")
                        .equals("You must Punch In")){
                    val pDialog1 = SweetAlertDialog(this@PermissionSelfConfirmActivity,
                        SweetAlertDialog.NORMAL_TYPE)
                    pDialog1.titleText = json.getString("message")
                    pDialog1.setCancelable(true)
                    pDialog1.setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener { sweetAlertDialog ->
                            sweetAlertDialog.dismiss()
                            val navController = MainActivity.navController
                            navController.navigate(R.id.nav_permission)
                            finish()
                        })
                    pDialog1.show()
                }
            }
            e.printStackTrace()
        } finally {
            withContext(Dispatchers.Main) {
                loaderLayout.visibility = View.GONE
                retake.visibility = View.VISIBLE
            }
        }
    }

    fun startAttendanceIn() {
        lifecycleScope.launch {
            attendanceIn()
        }
    }
}
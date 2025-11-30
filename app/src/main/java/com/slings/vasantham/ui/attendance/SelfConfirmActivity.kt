package com.slings.vasantham.ui.attendance

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.slings.vasantham.AttendanceSucess
import com.slings.vasantham.BaseActivity
import com.slings.vasantham.Common
import com.slings.vasantham.R
import com.slings.vasantham.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.InputStream
import androidx.core.net.toUri

class SelfConfirmActivity : BaseActivity() {

    private lateinit var imageView: ImageView
    private lateinit var loaderLayout: ConstraintLayout
    private lateinit var retake: Button
    private val client = OkHttpClient()

    private lateinit var imageUri: Uri
    private lateinit var filename: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selfie_confirm)

        // Get URI from SelfieAttendance
        val uriString = intent.getStringExtra("imageUri")
        imageUri = uriString!!.toUri()

        // Extract filename from URI
        filename = imageUri.lastPathSegment ?: "selfie.jpg"

        initUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUI() {
        imageView = findViewById(R.id.imageView)
        loaderLayout = findViewById(R.id.loader_layout)
        retake = findViewById(R.id.retake)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Go Back"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        findViewById<Button>(R.id.upload).setOnClickListener {
            retake.visibility = View.GONE
            showProgressDialog()
        }

        retake.setOnClickListener {
            startActivity(Intent(this, SelfieAttendance::class.java))
            finish()
        }

        // Load URI image
        Glide.with(this)
            .load(imageUri)
            .into(imageView)
    }

    private fun showProgressDialog() {
        loaderLayout.visibility = View.VISIBLE
        startAttendanceIn()
    }

    // ---------------------------------------------------------
    // Upload using URI InputStream (Scoped Storage Safe)
    // ---------------------------------------------------------
    private suspend fun attendanceIn() {
        var json: JSONObject? = null
        var response: Response? = null

        try {
            // Read compressed selfie from Saved MediaStore URI
            val inputStream: InputStream =
                contentResolver.openInputStream(imageUri)
                    ?: throw Exception("Cannot access image stream")

            val imageBytes = inputStream.readBytes()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "userId",
                    Util.getPreference(this, "userId", "") ?: ""
                )
                .addFormDataPart(
                    "shiftId",
                    Util.getPreference(this, "shiftId", 0).toString()
                )
                .addFormDataPart("associatedId", "30000")
                .addFormDataPart(
                    "imageUrl",
                    filename,
                    imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url(Common.URL + "staff/attendance/in")
                .post(requestBody)
                .build()

            response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (!response!!.isSuccessful)
                throw Exception("HTTP Error: ${response.code}")

            val responseBody = response.body?.string()
            json = JSONObject(responseBody!!)

            val data = json.getJSONObject("data")

            withContext(Dispatchers.Main) {
                val intent = Intent(
                    this@SelfConfirmActivity,
                    AttendanceSucess::class.java
                )
                intent.putExtra("lateBy", data.getString("lateBy"))
                startActivity(intent)
                finish()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@SelfConfirmActivity,
                    "Upload failed",
                    Toast.LENGTH_LONG
                ).show()
            }
            e.printStackTrace()
        } finally {
            withContext(Dispatchers.Main) {
                loaderLayout.visibility = View.GONE
                retake.visibility = View.VISIBLE
            }
        }
    }

    private fun startAttendanceIn() {
        lifecycleScope.launch {
            attendanceIn()
        }
    }
}

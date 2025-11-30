package com.slings.vasantham.ui.attendance

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.OpenableColumns
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
import com.slings.vasantham.BaseActivity
import com.slings.vasantham.Common
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.InputStream

class PermissionSelfConfirmActivity : BaseActivity() {

    private lateinit var imageView: ImageView
    private val client = OkHttpClient()
    lateinit var loaderLayout: ConstraintLayout
    lateinit var retake: Button

    private lateinit var imageUri: Uri
    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.selfie_confirm)

        // Receive URI string in imageURL (we kept the same key)
        val uriString = intent.getStringExtra("imageURL") ?: ""
        imageUri = Uri.parse(uriString)
        fileName = resolveFilename(imageUri)

        initUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
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

        retake = findViewById(R.id.retake)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        findViewById<Button>(R.id.upload).setOnClickListener {
            retake.visibility = View.GONE
            showProgressDialog()
        }

        retake.setOnClickListener {
            val intent = Intent(this, PermissionSelfieAttendance::class.java)
            startActivity(intent)
            finish()
        }

        Glide.with(this)
            .load(imageUri)
            .into(imageView)
    }

    private fun showProgressDialog() {
        loaderLayout.visibility = View.VISIBLE
        startAttendanceIn()
    }

    // Try to get a human filename for the Uri. Fallback to timestamp name.
    private fun resolveFilename(uri: Uri): String {
        // 1) Try OpenableColumns
        var name: String? = null
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) name = cursor.getString(index)
            }
        }

        if (!name.isNullOrBlank()) return name!!

        // 2) Try lastPathSegment
        val last = uri.lastPathSegment
        if (!last.isNullOrBlank()) return last

        // 3) Fallback
        return "selfie_${System.currentTimeMillis()}.jpg"
    }

    // Upload using bytes read from the URI; keep field name "imageUrl"
    suspend fun attendanceIn() {
        var response: Response? = null
        var json: JSONObject? = null

        try {
            val inputStream: InputStream = contentResolver.openInputStream(imageUri)
                ?: throw Exception("Cannot open image stream")

            val imageBytes = inputStream.readBytes()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reason", "Permission")
                .addFormDataPart(
                    "imageUrl",
                    fileName,
                    imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .build()

            val url = Common.URL + "staff/permission/" +
                    Util.getPreference(applicationContext, "userId", "").toString()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            // If unauthorized or error, throw to catch
            if (!response.isSuccessful) {
                val body = response.body?.string()
                throw Exception("HTTP Error ${response.code}" + (if (body != null) " : $body" else ""))
            }

            val responseBody = response.body?.string() ?: "{}"
            json = JSONObject(responseBody)
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
            // Log and show helpful message if 401
            val err = e.message ?: "Upload failed"
            withContext(Dispatchers.Main) {
                // Show the message (if 401, server message included)
                Toast.makeText(this@PermissionSelfConfirmActivity, err, Toast.LENGTH_LONG).show()
                // If server responded with 401, guide to re-login (optional)
                if (err.contains("HTTP Error 401")) {
                    // Optionally clear session or prompt login — keep simple here
                    Toast.makeText(this@PermissionSelfConfirmActivity, "Unauthorized (401). Please check login.", Toast.LENGTH_LONG).show()
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

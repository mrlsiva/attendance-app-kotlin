package com.slings.vasantham.ui.attendance

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.slings.vasantham.AttendanceSucess
import com.slings.vasantham.Common
import com.slings.vasantham.R
import com.slings.vasantham.Util
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class SelfConfirmActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val client = OkHttpClient()
    //    lateinit var imageUri: Uri
//    lateinit var imageByteArray: ByteArray
    lateinit var loaderLayout: ConstraintLayout
//    lateinit var progr_per: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selfie_confirm)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        imageView = findViewById(R.id.imageView)
        val upload = findViewById<Button>(R.id.upload)
        val retake = findViewById<Button>(R.id.retake)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        loaderLayout = findViewById<ConstraintLayout>(R.id.loader_layout)
        setSupportActionBar(toolbar)
        toolbar.title = "Go Back"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        upload.setOnClickListener {
            showProgressDialog()

        }

        retake.setOnClickListener {
            val intent = Intent(this, SelfieAttendance::class.java)
            startActivity(intent)
            finish()
        }
        Glide.with(this)
            .load(intent.getStringExtra("imageURL"))
            .into(imageView);
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // Handle the back button press here
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showProgressDialog() {
        loaderLayout.visibility = View.VISIBLE
        val handler = Handler(Looper.getMainLooper())
        val backgroundTask1 = Runnable {
            handler.post {
                attendenaceIn()
            }
        }
        handler.postDelayed(backgroundTask1, 800)
    }

    fun excep(title: Unit){
//        val pDialog = SweetAlertDial]'
//                finish()
//            })
//        pDialog.show()
        val alertDialog = AlertDialog.Builder(
            SelfConfirmActivity@this)
        alertDialog.setMessage("Alert")
        alertDialog.setTitle(title.toString())
        alertDialog.setCancelable(true)
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, which -> }

        alertDialog.create().show()
    }


    fun attendenaceIn() {
        lateinit var response: Response
        val bufferSize = 4096
        val buffer = ByteArray(bufferSize)
        lateinit var json : JSONObject
        try {
            val mediaType: MediaType = "image/*".toMediaTypeOrNull()
                ?: throw IllegalArgumentException("Invalid media type")
            val imageFile = File(intent.getStringExtra("imageURL")!!)
//            val totalFileSize = imageByteArray.size
            var uploadedBytes: Long = 0

            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "userId",
                    Util.getPreference(applicationContext, "userId", "") ?: ""
                )
                .addFormDataPart(
                    "shiftId",
                    Util.getPreference(applicationContext, "shiftId", "") ?: ""
                )
                .addFormDataPart("associatedId", "30000")
                .addFormDataPart("imageUrl", imageFile.name, RequestBody.create("image/*".toMediaTypeOrNull(), imageFile))
                .build()

            val request = Request.Builder()
                .url(
                    Common.URL +"api/staff/attendance/in")
                .post(requestBody)
                .build()

            response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                // Handle HTTP error (e.g., response.code())
                throw IOException("HTTP Error: ${response.code}")
            }

        } catch (e: IOException) {
            // Handle network-related errors
            Toast.makeText(applicationContext,""+response.body?.string(),Toast.LENGTH_LONG).show()
            excep(e.printStackTrace())
            e.printStackTrace()
        } catch (e: JSONException) {
            // Handle JSON parsing errors
            Toast.makeText(applicationContext,""+response.body?.string(),Toast.LENGTH_LONG).show()
            excep(e.printStackTrace())
            e.printStackTrace()
        } catch (e: Exception) {
            // Handle other unexpected exceptions
            Toast.makeText(applicationContext,""+response.body?.string(),Toast.LENGTH_LONG).show()
            excep(e.printStackTrace())
            e.printStackTrace()
        } finally {
//            response?.body?.close() // Close the response body to release resources
            loaderLayout.visibility = View.GONE
            try{
                val responseBody = response.body?.string()
                json = JSONObject(responseBody)
                val data = json.getJSONObject("data")
                val intent = Intent(this, AttendanceSucess::class.java)
                intent.putExtra("lateBy",data.getString("lateBy"))
                startActivity(intent)
                finish()
            }catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }
}

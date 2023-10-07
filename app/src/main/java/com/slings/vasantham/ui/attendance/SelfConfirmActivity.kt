package com.slings.vasantham.ui.attendance

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.slings.vasantham.AttendanceSucess
import com.slings.vasantham.Common
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.buffer
import okio.source
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Timer
import java.util.TimerTask


class SelfConfirmActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val client = OkHttpClient()
//    lateinit var imageUri: Uri
    lateinit var imageByteArray: ByteArray
    private var progressDialog: AlertDialog? = null // Declare as a class variable
    lateinit var progressBar: ProgressBar
    lateinit var progr_per: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selfie_confirm)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        imageView = findViewById(R.id.imageView)
        val upload = findViewById<Button>(R.id.upload)
        val retake = findViewById<Button>(R.id.retake)
        val tvGoBack = findViewById<TextView>(R.id.tvGoBack)

        upload.setOnClickListener {
            showProgressDialog()

        }

        retake.setOnClickListener {
            val intent = Intent(this, SelfieAttendance::class.java)
            startActivity(intent)
            finish()
        }

        tvGoBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Get the captured image from the intent
       val imageUri  = intent.getParcelableArrayExtra("capturedImage")!!
       val byteArrayOutputStream = ByteArrayOutputStream()
        contentResolver.openInputStream(imageUri)?.use { stream ->
            val bitmap = BitmapFactory.decodeStream(stream)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            byteArray?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
//                imageView.setImageBitmap(intent.getByteArrayExtra("capturedImage")!!)
            }
        }
        val bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false))

    }


    private fun showProgressDialog() {

        val handler = Handler(Looper.getMainLooper())
        val backgroundTask = Runnable {
            handler.post {
                val builder = AlertDialog.Builder(this@SelfConfirmActivity)
                builder.setView(R.layout.progress)
                builder.setCancelable(true)
                progressDialog = builder.create()
                progressDialog?.show()
                progressBar = progressDialog!!.findViewById(R.id.progressBar)!!
                progr_per = progressDialog!!.findViewById(R.id.progr_per)!!
            }
        }
        Thread(backgroundTask).start()

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
            applicationContext)
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
//            val imageFile = File(imageUri.path!!)
            val totalFileSize = imageByteArray.size
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
                .addFormDataPart("imageUrl", imageUri.lastPathSegment, object : RequestBody() {
                    override fun contentType(): MediaType? = mediaType

                    override fun contentLength(): Long = totalFileSize

                      override fun writeTo(sink: BufferedSink) {
                        val source = imageFile.source().buffer() // Wrap the source in a buffer
                        var bytesRead: Long
                        while (source.read(buffer).also { bytesRead = it.toLong() }
                                .toLong() != -1L) {
                            sink.write(buffer, 0, bytesRead.toInt())
                            uploadedBytes += bytesRead

                            // Calculate and update progress
                            val progress = (uploadedBytes * 100 / totalFileSize).toInt()
                            // Ensure you update the progress bar on the ma\
                            // in thread
                            Timer().scheduleAtFixedRate(object : TimerTask() {
                                override fun run() {
                                        progressBar.progress = progress
//                                        progr_per.text = progress.toString()
                                }
                            }, 1000, 1000)
                        }
                        source.close()
                    }
                })
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
            response?.body?.close() // Close the response body to release resources
            progressDialog?.dismiss()
           try{
               val responseBody = response.body?.string()
               json = JSONObject(responseBody)
               val data = json.getJSONObject("data")
               Toast.makeText(applicationContext,""+data,Toast.LENGTH_LONG).show()
               val intent = Intent(this, AttendanceSucess::class.java)
               intent.putExtra("lateBy",data.getString("lateBy"))
               startActivity(intent)
               finish()
           }catch (e:Exception) {
               e.printStackTrace()
               Toast.makeText(applicationContext,""+response.body?.string(),Toast.LENGTH_LONG).show()
           }
        }
}
}


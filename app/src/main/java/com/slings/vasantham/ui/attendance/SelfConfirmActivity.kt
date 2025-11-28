package com.slings.vasantham.ui.attendance

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.slings.vasantham.AttendanceSucess
import com.slings.vasantham.BaseActivity
import com.slings.vasantham.Common
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

class SelfConfirmActivity : BaseActivity() {

    private lateinit var imageView: ImageView
    private val client = OkHttpClient()
    lateinit var loaderLayout: ConstraintLayout
    lateinit var destFile: File
    lateinit var retake: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selfie_confirm)
        destFile = File(intent.getStringExtra("imageURL"))
        initUI()
//        initImagecompress()
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
        loaderLayout = findViewById<ConstraintLayout>(R.id.loader_layout)
        setSupportActionBar(toolbar)
        toolbar.title = "Go Back"

        retake =  findViewById<Button>(R.id.retake)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        findViewById<Button>(R.id.upload).setOnClickListener {
            retake.visibility = View.GONE
            showProgressDialog()
        }

        retake.setOnClickListener {
            val intent = Intent(this, SelfieAttendance::class.java)
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
        /*   val handler = Handler(Looper.getMainLooper())
        val backgroundTask1 = Runnable {
            handler.post {
                attendenaceIn()
            }
        }
        handler.postDelayed(backgroundTask1, 800)*/
    }

    /*  fun initImagecompress(){
        if(filesize(destFile)>2){
            resizeAndCompressImageWithGlide(destFile, File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                intent.getStringExtra("imagename")+"_1.jpg"))
            destFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                intent.getStringExtra("imagename")+"_1.jpg")
        }
    }*/

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
                .addFormDataPart(
                    "userId",
                    Util.getPreference(applicationContext, "userId", "") ?: ""
                )
                .addFormDataPart(
                    "shiftId",
                    (Util.getPreference(applicationContext, "shiftId", 0) ?: "").toString()
                )
                .addFormDataPart("associatedId", "30000")
                .addFormDataPart(
                    "imageUrl",
                    imageFile.name,
                    imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url(Common.URL + "staff/attendance/in")
                .post(requestBody)
                .build()

            withContext(Dispatchers.IO) {
                response = client.newCall(request).execute()
            }

            if (!response.isSuccessful) {
                throw IOException("HTTP Error: ${response.code}")
            }

                val responseBody = response.body?.string()
            json = JSONObject(responseBody)
            val data = json.getJSONObject("data")

            withContext(Dispatchers.Main) {
                val intent = Intent(this@SelfConfirmActivity, AttendanceSucess::class.java)
                intent.putExtra("lateBy", data.getString("lateBy"))
                startActivity(intent)
                finish()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "" + response.body?.string(), Toast.LENGTH_LONG)
                    .show()
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


    private fun resizeAndCompressImageWithGlide(imageUrl: File, destFile: File) {
        try {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .apply(
                    RequestOptions()
                    .override(1024, 1024)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true))
                .submit()

            val bitmap: Bitmap = futureTarget.get()

            FileOutputStream(destFile).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream)
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun filesize(file:File):Long{
        if (file.exists()) {
            val fileSizeInBytes: Long = file.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024
            return fileSizeInMB;
        } else {
            return 0L;
            println("File not found!")
        }
    }
}



/*  fun attendenaceIn() {
    lateinit var response: Response
    val bufferSize = 4096
    val buffer = ByteArray(bufferSize)
    lateinit var json : JSONObject
    val imageFile = File(intent.getStringExtra("imageURL")!!)
    try {
        val mediaType: MediaType = "image/*".toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type")
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
                (Util.getPreference(applicationContext, "shiftId", 0) ?: "").toString()
            )
            .addFormDataPart("associatedId", "30000")
            .addFormDataPart("imageUrl", destFile.name,
                destFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()


        val request = Request.Builder()
            .url(
                Common.URL +"staff/attendance/in")
            .post(requestBody)
            .build()

        response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            // Handle HTTP error (e.g., response.code())
            throw IOException("HTTP Error: ${response.code}")
        }

    }  catch (e: Exception) {
        Toast.makeText(applicationContext,""+response.body?.string(),Toast.LENGTH_LONG).show()
        e.printStackTrace()
    } finally {
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
        retake.visibility = View.VISIBLE
    }
}*/
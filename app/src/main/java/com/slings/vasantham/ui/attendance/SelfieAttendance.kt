package com.slings.vasantham.ui.attendance

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Size
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.slings.vasantham.BaseActivity
import com.slings.vasantham.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SelfieAttendance : BaseActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var capturedImage: Bitmap? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null
    private lateinit var outputFile: File
    private lateinit var cameraView: PreviewView

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selfie_attendance)

        initUI()

        initCamera()
    }

    private fun initUI(){
        val btnTakeSelfie = findViewById<ImageView>(R.id.btnTakeSelfie)
        val tvGoBack = findViewById<ImageView>(R.id.tvGoBack)
        cameraView = findViewById<PreviewView>(R.id.cameraView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        toolbar.title = "Go Back"
        btnTakeSelfie.setOnClickListener {
            takePicture()
        }
        tvGoBack.setOnClickListener {
            finish()
        }
    }

    fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Bind the camera preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            try {
                camera?.cameraControl?.enableTorch(false) // Disable the torch if enabled before
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
        cameraExecutor = Executors.newSingleThreadExecutor()
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    data?.extras?.getByteArray("capturedImage")?.let { byteArray ->
                        var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                        // Compress the image to ensure it's less than 2 MB
                        var quality = 100
                        do {
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                            val compressedByteArray = byteArrayOutputStream.toByteArray()

                            if (compressedByteArray.size < 2 * 1024 * 1024) { // 2 MB
                                capturedImage = BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)
                                break
                            }
                            quality -= 5
                        } while (quality > 0)
                    }
                }
            }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    private fun takePicture() {

        val imageCapture = imageCapture ?: return
        outputFile = getOutputFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e(TAG, "Error capturing image: ${error.message}", error)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    showNextActivity()
                }
            })
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

    private fun getOutputFile(): File {
        if(!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Attendance/")!!.exists()){
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Attendance/")!!.mkdirs()
        }
        try{
            return File.createTempFile(
                "image",
                ".jpg",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Attendance/")
            )
        }catch (e:Exception) {
            e.printStackTrace()
        }
        return File.createTempFile(
            "image",
            ".jpg",
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Attendance/")
        )
    }

    private fun showNextActivity() {
        val intent = Intent(this, SelfConfirmActivity::class.java)
        intent.putExtra("imageURL", outputFile.absolutePath)
        intent.putExtra("imagename", outputFile.name)
        startActivity(intent)
        finish()
    }
}
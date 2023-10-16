package com.slings.vasantham.ui.attendance

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.slings.vasantham.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SelfieAttendance : AppCompatActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var capturedImage: Bitmap? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null
    private lateinit var outputFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selfie_attendance)

        val btnTakeSelfie = findViewById<ImageView>(R.id.btnTakeSelfie)
        val tvGoBack = findViewById<ImageView>(R.id.tvGoBack)
        val cameraView = findViewById<PreviewView>(R.id.cameraView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        toolbar.title = "Go Back"
        // Initialize the camera view and the "Take Selfie" button
        // (Assuming you have added necessary dependencies for CameraX)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Bind the camera preview
            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(cameraView.surfaceProvider)
                }

            // Bind the image capture use case to the lifecycle
            imageCapture = ImageCapture.Builder().build()

            try {
                // Unbind any previously bound use cases
                camera?.cameraControl?.enableTorch(false) // Disable the torch if enabled before
                cameraProvider.unbindAll()

                // Bind the camera to the lifecycle
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                btnTakeSelfie.setOnClickListener {
                    takePicture()
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))

        // Initialize the cameraExecutor for background tasks
        cameraExecutor = Executors.newSingleThreadExecutor()

        btnTakeSelfie.setOnClickListener {
            takePicture()
            Toast.makeText(this, "ImageView Clicked!", Toast.LENGTH_SHORT).show()
        }

        // Set up the "Go Back" TextView click listener
        tvGoBack.setOnClickListener {
            finish()
        }

        // Set up the activity result launcher for the next activity
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    data?.extras?.getByteArray("capturedImage")?.let { byteArray ->
                        capturedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                        showNextActivity(output)
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
                    showNextActivity(output)
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
        try{
            return File.createTempFile(
                "image",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            )
        }catch (e:Exception) {
            e.printStackTrace()
        }
        return File.createTempFile(
            "image",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        )
    }

    private fun showNextActivity(output: ImageCapture.OutputFileResults) {
        // Launch the next activity and pass the captured image as a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        capturedImage?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        val iStream = contentResolver.openInputStream(output.savedUri!!)
//        val inputData: ByteArray = getBytes(iStream!!)!!
        val intent = Intent(this, SelfConfirmActivity::class.java)
//        intent.putExtra("capturedImage", inputData)
        intent.putExtra("imageURL", outputFile.absolutePath)
        intent.putExtra("imagename", outputFile.name)
        startActivity(intent)
        finish()
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 4096
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }
}
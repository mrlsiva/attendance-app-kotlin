package com.slings.vasantham.ui.attendance

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.slings.vasantham.BaseActivity
import com.slings.vasantham.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PermissionSelfieAttendance : BaseActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var capturedImage: Bitmap? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null
    private lateinit var outputFile: File        // ← KEPT to avoid rename, NOT USED
    private lateinit var cameraView: PreviewView

    private var savedImageUri: Uri? = null       // NEW actual result URI

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selfie_attendance)

        initUI()
        initCamera()
    }

    private fun initUI() {
        val btnTakeSelfie = findViewById<ImageView>(R.id.btnTakeSelfie)
        val tvGoBack = findViewById<ImageView>(R.id.tvGoBack)
        cameraView = findViewById(R.id.cameraView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
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
            val provider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                provider.unbindAll()
                camera = provider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    // ------------------------------------------------------
    // Convert ImageProxy → Bitmap
    // ------------------------------------------------------
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    // ------------------------------------------------------
    // Compress your selfie <2MB (same code as your old one)
    // ------------------------------------------------------
    private fun compressBitmapUnder2MB(bitmap: Bitmap): ByteArray {
        var quality = 100
        var compressedBytes: ByteArray

        do {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            compressedBytes = baos.toByteArray()

            if (compressedBytes.size < 2 * 1024 * 1024) break
            quality -= 5

        } while (quality > 5)

        return compressedBytes
    }

    // ------------------------------------------------------
    // Create MediaStore file (replaces File() creation)
    // ------------------------------------------------------
    private fun getOutputFile(): File {
        // FUNCTION MUST EXIST → return dummy File
        return File(cacheDir, "dummy.jpg")
    }

    private fun createMediaStoreUri(): Uri? {
        val filename = "selfie_" + System.currentTimeMillis() + ".jpg"

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, "image/jpeg")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/Attendance/")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        return contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    }

    // ------------------------------------------------------
    // Capture selfie using callback instead of file
    // ------------------------------------------------------
    private fun takePicture() {
        val capture = imageCapture ?: return

        capture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {

                override fun onCaptureSuccess(image: ImageProxy) {
                    val bmp = imageProxyToBitmap(image)
                    val compressed = compressBitmapUnder2MB(bmp)
                    image.close()

                    val uri = createMediaStoreUri()
                    if (uri == null) {
                        Toast.makeText(this@PermissionSelfieAttendance,
                            "Failed to store image", Toast.LENGTH_SHORT).show()
                        return
                    }

                    contentResolver.openOutputStream(uri)?.use {
                        it.write(compressed)
                    }

                    // Mark file complete
                    val done = ContentValues().apply {
                        put(MediaStore.Downloads.IS_PENDING, 0)
                    }
                    contentResolver.update(uri, done, null, null)

                    savedImageUri = uri
                    showNextActivity()
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Error capturing image: ${exc.message}", exc)
                }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // ------------------------------------------------------
    // Pass URI instead of file path, BUT KEEP SAME NAME
    // ------------------------------------------------------
    private fun showNextActivity() {
        val intent = Intent(this, PermissionSelfConfirmActivity::class.java)
        intent.putExtra("imageURL", savedImageUri.toString())    // SAME KEY
        intent.putExtra("imagename", "selfie.jpg")               // SAFE DEFAULT
        startActivity(intent)
        finish()
    }
}

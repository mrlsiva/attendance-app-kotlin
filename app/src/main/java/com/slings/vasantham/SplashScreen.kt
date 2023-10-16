package com.slings.vasantham

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hasCameraStoragePermissions()) {
                // Permissions are already granted, proceed with a delay
                delayAndContinue()
            } else {
                requestCameraAndWriteStoragePermissions()
            }
        } else {
            if (hasCameraAndWriteStoragePermissions()) {
                // Permissions are already granted, proceed with a delay
                delayAndContinue()
            } else {
                requestCameraAndWriteStoragePermissions()
            }
        }
    }

    private fun hasCameraAndWriteStoragePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun hasCameraStoragePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(Manifest.permission.CAMERA),
            Companion.CAMERA_WRITE_STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestCameraAndWriteStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            Companion.CAMERA_WRITE_STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun delayAndContinue() {
        Handler().postDelayed({
            if(Util.getPreference(applicationContext,"userId","").equals("")){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // 3 seconds delay
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Companion.CAMERA_WRITE_STORAGE_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions granted, proceed with a delay before continuing
                    delayAndContinue()
                } else {

                    // Permissions denied, show a toast or handle it as you prefer
                    Toast.makeText(
                        this, "Camera and Write Storage permissions are required.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // You might want to close the app or take appropriate action here
                }
            }else{
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions granted, proceed with a delay before continuing
                    delayAndContinue()
                } else {

                    // Permissions denied, show a toast or handle it as you prefer
                    Toast.makeText(
                        this, "Camera and Write Storage permissions are required.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // You might want to close the app or take appropriate action here
                }
            }

        }
    }

    companion object {
        private const val CAMERA_WRITE_STORAGE_PERMISSION_REQUEST_CODE = 100
    }
}

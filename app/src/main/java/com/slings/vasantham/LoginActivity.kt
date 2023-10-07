package com.slings.vasantham

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.slings.vasantham.Common.Companion.URL
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    lateinit var usernameEditText: TextInputEditText
    lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val submitBtn = findViewById<Button>(R.id.submitBtn)
        usernameEditText = findViewById<TextInputEditText>(R.id.usernameEditText)
        passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)

        // Set the click listener for the submit button
        submitBtn.setOnClickListener {
            try {
                val formBody: RequestBody = FormBody.Builder()
                    .add("mobile", usernameEditText.text.toString())
                    .add("password", passwordEditText.text.toString())
                    .build()
                val request =
                    Request.Builder().url(URL+"api/login").post(formBody).build()
                try {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody)

                    if (json.has("success")) {
                        val body = json.getJSONObject("success")
                        val userId = body.getString("userId")
                        val Uname = body.getString("username")
                        val shiftName = body.getString("shiftname")
                        val token = body.getString("token")

                        Util.setPreference(applicationContext,"mobile",usernameEditText.text.toString())
                        Util.setPreference(applicationContext,"password",passwordEditText.text.toString())
                        Util.setPreference(applicationContext,"userId",userId)
                        Util.setPreference(applicationContext,"username",Uname)
                        Util.setPreference(applicationContext,"shiftname",shiftName)
                        Util.setPreference(applicationContext,"token",token)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
                        Toast.makeText(applicationContext,"Invalid mobileNo or Password",Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext,"Invalid mobileNo or Password",Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext,"Invalid mobileNo or Password",Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
            // Replace the login logic with your actual login validation here
            /*     val username = usernameEditText.text.toString()
                 val password = passwordEditText.text.toString()

                 // For simplicity, let's assume the login is successful if both fields are non-empty
                 if (username.isNotEmpty() && password.isNotEmpty()) {*/
            // Login successful, redirect to the Dashboard activity
            // Replace DashboardActivity::class.java with your actual Dashboard activity

            /*} else {
                // Handle login failure here (e.g., show an error message)
            }*/
        }
    }
}

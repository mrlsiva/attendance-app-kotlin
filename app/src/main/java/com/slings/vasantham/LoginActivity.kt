package com.slings.vasantham

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.slings.vasantham.ViewModel.LoginViewModel
import com.slings.vasantham.service.RetrofitService


class LoginActivity : BaseActivity() {

    //    private val client = OkHttpClient()
    lateinit var usernameEditText: AppCompatEditText
    lateinit var passwordEditText: EditText
    lateinit var viewModel: LoginViewModel
    lateinit var sweetAlertDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val submitBtn = findViewById<Button>(R.id.submitBtn)
        usernameEditText = findViewById<AppCompatEditText>(R.id.usernameEditText)
        passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val passwordEditText = findViewById<AppCompatEditText>(R.id.passwordEditText)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        val retrofitService = RetrofitService.getInstance()

        sweetAlertDialog = SweetAlertDialog(
            this@LoginActivity,
            SweetAlertDialog.PROGRESS_TYPE
        )
        sweetAlertDialog.setTitleText("Loading")
        viewModel =
            ViewModelProvider(this)[LoginViewModel::class.java]
        viewModel.logindata.observe(this, Observer {
            try{
                it.success.let {
                    try {
                        Util.setPreference(
                            applicationContext,
                            "mobile",
                            usernameEditText.text.toString()
                        )
                        Util.setPreference(
                            applicationContext,
                            "password",
                            passwordEditText.text.toString()
                        )
                        Util.setPreference(applicationContext,  "userId", it!!.userId.toString())
                        Util.setPreference(applicationContext, "username", it!!.username)
                        Util.setPreference(
                            applicationContext, "shiftname", it!!.startTime
                                    + " - " + it!!.endTime)
                        Util.setPreference(applicationContext, "token", it!!.token)
                        if (sweetAlertDialog.isShowing) {
                            sweetAlertDialog.dismiss()
                        }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        if (sweetAlertDialog.isShowing) {
                            sweetAlertDialog.dismiss()
                        }
                        Toast.makeText(
                            applicationContext,
                            "Invalid mobileNo or Password",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                it.error.let {
                    if (sweetAlertDialog.isShowing) {
                        sweetAlertDialog.dismiss()
                    }
//                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }
            }catch (Exception:Exception){
                if (sweetAlertDialog.isShowing) {
                    sweetAlertDialog.dismiss()
                }
                Toast.makeText(
                    applicationContext,
                    "Invalid mobileNo or Password",
                    Toast.LENGTH_LONG
                ).show()
            }
        })


        passwordToggle.setOnClickListener { v: View? ->
            val selection = passwordEditText.selectionEnd
            if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // If password is visible, hide it
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.eye_icon_strike)
            }else{
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.eye)
            }

            // Maintain cursor position
            passwordEditText.setSelection(selection)
        }

        // Set the click listener for the submit button
        submitBtn.setOnClickListener {

            if (!sweetAlertDialog.isShowing) {
                sweetAlertDialog.show()
            }

            viewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                retrofitService, Build.MODEL+","+Build.VERSION.RELEASE+","
                        +BuildConfig.VERSION_NAME
            )
        }
    }
}

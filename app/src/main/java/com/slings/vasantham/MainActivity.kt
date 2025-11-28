package com.slings.vasantham

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.slings.vasantham.ViewModel.LoginViewModel
import com.slings.vasantham.ViewModel.MainViewModel
import com.slings.vasantham.ViewModelFactory.MyViewModelFactory
import com.slings.vasantham.databinding.ActivityMainBinding
import com.slings.vasantham.service.RetrofitService
import com.zagori.bottomnavbar.BottomNavBar
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()
    lateinit var toolbar_end_item: ImageView
    lateinit var viewModel: MainViewModel
    lateinit var sweetAlertDialog: SweetAlertDialog
    lateinit var retrofitService:RetrofitService
    lateinit var headerView: View

    lateinit var drawerLayout: DrawerLayout

    companion object {
        lateinit var navController: NavController
        lateinit var navUsername: TextView
        lateinit var designation: TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
         headerView = binding.navigationView.getHeaderView(0)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false);
        supportActionBar!!.setHomeButtonEnabled(false);
        toolbar_end_item = findViewById(R.id.toolbar_end_item)
        binding.versionNo.text = "Version " + BuildConfig.VERSION_NAME.toString()

        val inflater = LayoutInflater.from(this)
        val customTitleView: View = inflater.inflate(R.layout.custom_toolbar_title, null)
        val customTitleTextView = customTitleView.findViewById<TextView>(R.id.toolbar_title)
        customTitleTextView.text = getString(R.string.app_name)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navigationView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_attendance
            ), drawerLayout
        )

        binding.logout.setOnClickListener {
            showLogoutDialog()
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            NavigationUI.onNavDestinationSelected(menuItem, navController)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        retrofitService = RetrofitService.getInstance()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getShifTime().observe(this, Observer {
            try {
                if (it.success) {
                    val parser = SimpleDateFormat("HH:mm:ss")
                    val formatter = SimpleDateFormat("HH:mm a")
                    val shiftStart = formatter.format(parser.parse(it.data.startTime))
                    val shiftEnd = formatter.format(parser.parse(it.data.endTime))
                    Util.setPreference(applicationContext, "shiftId", it.data.shiftId)
                    Util.setPreference(applicationContext, "shiftStart", shiftStart)
                    Util.setPreference(applicationContext, "shiftEnd", shiftEnd)
                    val parser1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val formatter1 = SimpleDateFormat("EEEE, MMMM dd, yyyy")
                    val output1 = formatter1.format(parser1.parse(it.data.currentTime))
                    Util.setPreference(applicationContext, "currentFullTime", output1)
//                    Util.setPreference(applicationContext, "shiftStartTime", it.data.startTime)
//                    Util.setPreference(applicationContext, "shiftEndTime", it.data.endTime)
                    val parser2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val formatter2 = SimpleDateFormat("HH:mm a")
                    val output2 = formatter2.format(parser2.parse(it.data.currentTime))
                    Util.setPreference(applicationContext, "CurrentOnlyTime", output2)
                }
            } catch (e: Exception) {
//                Toast.makeText(
//                    applicationContext,
//                    "Network error",
//                    Toast.LENGTH_LONG
//                ).show()
            }
        })
        try {
            viewModel.profileEditLoad(
                Util.getPreference(applicationContext, "userId", "").toString(),
                retrofitService!!
            )
            viewModel.getprofileLiveData().observe(this, Observer {
                try {
                    if (it.success) {
                        Util.setPreference(
                            applicationContext,"profileUrl",
                            it.data.userImageUrl)
                        runOnUiThread {
                            Glide.with(this)
                                .load(Util.getPreference(applicationContext,"profileUrl",""))
                                .error(com.slings.vasantham.R.drawable.menu_user_profile)
                                .into(headerView.findViewById(R.id.imageView))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                Util.setPreference(applicationContext, "mobile", "")
                Util.setPreference(applicationContext, "password", "")
                Util.setPreference(applicationContext, "userId", "")
                Util.setPreference(applicationContext, "username", "")
                Util.setPreference(applicationContext, "shiftname", "")
                Util.setPreference(applicationContext, "token", "")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()

        navUsername = headerView.findViewById(R.id.menu_name) as TextView
        designation = headerView.findViewById(R.id.menu_desgn) as TextView

        Util.profileUri?.let { it1 ->
            Glide.with(this)
                .load(Util.profileUri)
                .error(R.drawable.menu_user_profile)
                .into(headerView.findViewById(R.id.imageView))
        } ?: run {
            {
                Glide.with(this)
                    .load(Util.getPreference(applicationContext, "profileUrl", ""))
                    .error(R.drawable.menu_user_profile)
                    .into(headerView.findViewById(R.id.imageView))
            }
        }

        navUsername.text = Util.getPreference(applicationContext, "username", "")
        designation.text = Util.getPreference(
            applicationContext,
            "designation", "-"
        )
        headerView.setOnClickListener(View.OnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.nav_profilefragment)
        })
        try {
            if (intent.getBooleanExtra("openAttLog", false)) {
                navController.navigate(R.id.nav_gallery)
            }
        } catch (e: Exception) {

        }

        viewModel.getShift(
            Util.getPreference(applicationContext,"userId","").toString()
            ,retrofitService
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
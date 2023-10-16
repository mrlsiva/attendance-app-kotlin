package com.slings.vasantham

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.slings.vasantham.databinding.ActivityMainBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val drawerArrow = DrawerArrowDrawable(this)
//        drawerArrow.color = resources.getColor(R.color.black,null)
//                yourImageView.setImageDrawable(drawerArrow)
        setSupportActionBar(binding.appBarMain.toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false);
        supportActionBar!!.setHomeButtonEnabled(false);

        val inflater = LayoutInflater.from(this)
        val customTitleView: View = inflater.inflate(R.layout.custom_toolbar_title, null)

// Set the custom view as the title
        val customTitleTextView = customTitleView.findViewById<TextView>(R.id.toolbar_title)
        customTitleTextView.text = getString(R.string.app_name)
// Set the custom view as the title
//        supportActionBar!!.customView = customTitleView

        try {
            val formBody: RequestBody = FormBody.Builder()
                .add("userId", Util.getPreference(applicationContext.applicationContext,"userId","").toString())
                .build()
            val request =
                Request.Builder().url(Common.URL+"api/staff/getshift/"+
                        Util.getPreference(applicationContext,"userId","").toString()).post(formBody).build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody)
                if (json.has("success")) {
                    val data = json.getJSONObject("data")
                    val shiftId = data.getString("shiftId")

                    Util.setPreference(applicationContext,"shiftId",shiftId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        binding.logout.setOnClickListener{
            showLogoutDialog()
        }

        val headerView: View = binding.navView.getHeaderView(0)
        val navUsername = headerView.findViewById<TextView>(R.id.menu_name) as TextView
        navUsername.text = Util.getPreference(applicationContext
            .applicationContext,"username","")
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            // It's possible to do more actions on several items; if there's a large number of items, consider using a when statement instead of if


            // This is for maintaining the behavior of the Navigation view
            NavigationUI.onNavDestinationSelected(menuItem, navController)
            // This is for closing the drawer after acting on it
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                Util.setPreference(applicationContext,"mobile","")
                Util.setPreference(applicationContext,"password","")
                Util.setPreference(applicationContext,"userId","")
                Util.setPreference(applicationContext,"username","")
                Util.setPreference(applicationContext,"shiftname","")
                Util.setPreference(applicationContext,"token","")
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
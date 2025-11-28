package com.slings.vasantham.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.slings.vasantham.BuildConfig
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.slings.vasantham.Common
import com.slings.vasantham.MainActivity
import com.slings.vasantham.MainActivity.Companion.designation
import com.slings.vasantham.MainActivity.Companion.navUsername
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.databinding.FragmentHomeBinding
import com.zagori.bottomnavbar.BottomNavBar


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

      binding.attendanceButton.setOnClickListener {
          val navController = findNavController()
          navController.navigate(R.id.navigation_attendance)
      }

        try {
            binding.topbar.attenName.text = "Hi, "+Util.getPreference(requireActivity()
                .applicationContext,"username","")
            binding.topbar.attenDesgn.text = "Welcomes to Vasantham"
            Glide.with(this)
                .load(Util.getPreference(requireActivity(),"profileUrl",""))
                .error(com.slings.vasantham.R.drawable.menu_user_profile)
                .into(binding.topbar.imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.permissionButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.nav_permission)
        }

        binding.bottomNavView.setBottomNavigationListener(object :
            BottomNavBar.OnBottomNavigationListener {
            override fun onNavigationItemSelected(menuItem: MenuItem?): Boolean {
                return when (menuItem!!.itemId) {
                    R.id.navigation_home -> {
                        MainActivity.navController.navigate(R.id.nav_home)
                        true
                    }

                    R.id.navigation_users -> {
                        MainActivity.navController.navigate(R.id.nav_usersfragment)
                        true
                    }

                    R.id.navigation_dashboard -> {
                        MainActivity.navController.navigate(R.id.navigation_attendance)
                        false
                    }

                    R.id.navigation_profile -> {
                        MainActivity.navController.navigate(R.id.nav_profilefragment)
                        false
                    }

                    else -> false
                }
            }
        })

        binding.applyLeaveButton.setOnLongClickListener{
            binding.usersButton.visibility = View.VISIBLE
            binding.designaButton.visibility = View.VISIBLE
            binding.shiftButton.visibility = View.VISIBLE
            return@setOnLongClickListener true
        }

        binding.applyLeaveButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.nav_leave)
          /*  val pDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
            pDialog.titleText = "Coming Soon"
            pDialog.setCancelable(true)
            pDialog.show()*/
        }

        binding.usersButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.nav_usersfragment)
        }

        binding.designaButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.nav_designfragment)
        }

        binding.shiftButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.nav_shiftfragment)
        }
      /*  homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).toolbar_end_item.visibility = View.GONE
        navUsername.text = Util.getPreference(activity as MainActivity, "username", "")
        designation.text = Util.getPreference(
            activity as MainActivity,
            "designation", "-"
        )


//        if(Common.RedirectTo == "attendanceLog"){
//            val navController = findNavController()
//            navController.navigate(R.id.nav_gallery)
//        }
    }
}
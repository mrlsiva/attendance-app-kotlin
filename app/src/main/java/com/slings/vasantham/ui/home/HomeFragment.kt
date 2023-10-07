package com.slings.vasantham.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.slings.vasantham.R
import com.slings.vasantham.databinding.FragmentHomeBinding


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
          navController.navigate(R.id.nav_attendance)
      }

        binding.applyLeaveButton.setOnClickListener {
            val pDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
            pDialog.titleText = "Coming Soon"
            pDialog.setCancelable(true)
            pDialog.show()
        }


        binding.permissionButton.setOnClickListener {
            val pDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
            pDialog.titleText = "Coming Soon"
            pDialog.setCancelable(true)
            pDialog.show()
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
}
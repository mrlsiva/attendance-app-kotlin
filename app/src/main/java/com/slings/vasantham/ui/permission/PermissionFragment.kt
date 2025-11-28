package com.slings.vasantham.ui.permission

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.impl.utils.ContextUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.ViewModel.MainViewModel
import com.slings.vasantham.databinding.PermissionFragmentBinding
import com.slings.vasantham.service.RetrofitService
import com.slings.vasantham.ui.attendance.PermissionSelfieAttendance
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PermissionFragment : Fragment() {
    private var _binding: PermissionFragmentBinding? = null
    private val client = OkHttpClient()
    private val binding get() = _binding!!

    lateinit var viewModel: MainViewModel
    lateinit var sweetAlertDialog: SweetAlertDialog
    lateinit var retrofitService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        _binding = PermissionFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        try {
            binding.topbar.attenName.text = "Hi, " + Util.getPreference(
                requireActivity()
                    .applicationContext, "username", ""
            )
            binding.topbar.attenDesgn.text = "Welcomes to Vasantham"
            binding.textViewCurrentTime.text = currentTime
            binding.tvPlace.text = "Chennai"
            Glide.with(this)
                .load(Util.getPreference(requireActivity(),"profileUrl",""))
                .error(com.slings.vasantham.R.drawable.menu_user_profile)
                .into(binding.topbar.imageView)
            val shiftTimeText = "Your Shift time is: " + Util.getPreference(
                requireActivity()
                    .applicationContext, "shiftname", ""
            )
            if (Util.getPreference(
                    requireActivity()
                        .applicationContext, "shiftname", ""
                ).equals("Not Yet Allocated")
            ) {
                binding.punchLayout.visibility = View.GONE
            }
            val spannableString = SpannableString(shiftTimeText)
            val boldStyleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                boldStyleSpan,
                shiftTimeText.indexOf("is ") + 3,
                shiftTimeText.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.textViewShiftTime.text = spannableString
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.punchLayout.setOnClickListener { v: View? ->
            binding.punchLayout.isEnabled = false
            if (binding.punchToday.text.toString() == "Start Permission") {
                val intent = Intent(activity, PermissionSelfieAttendance::class.java)
                startActivity(intent)
                requireActivity().supportFragmentManager.popBackStack();
            } else {
                try {
                    viewModel.permissionApplyOut(
                        Util.getPreference(requireActivity().applicationContext, "userId", "")
                            .toString(),
                        retrofitService
                    )
                    if (!sweetAlertDialog.isShowing) {
                        sweetAlertDialog.show()
                    }

                    viewModel.getPermissionLiveData().observe(viewLifecycleOwner, Observer {
                        try {
                            if (sweetAlertDialog.isShowing) {
                                sweetAlertDialog.dismiss()
                            }

                            val pDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                            pDialog.titleText = "Permission End Successfully"
                            pDialog.setCancelable(true)
                            pDialog.setConfirmText("OK")
                                .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener { sweetAlertDialog ->
                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                    sweetAlertDialog.dismiss()
                                })
                            pDialog.show()


                            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                            val outputFormat = SimpleDateFormat("hh:mm aa", Locale.US)

                            val startTime = inputFormat.parse(it.data!!.permissionStartTime)
                            val formattedStartTime = startTime?.let { outputFormat.format(it) }
                            binding.permissionIntime.text = formattedStartTime

//                            it.data!!.permissionEndTime.let {it->
                                val endTime = it.data!!.permissionEndTime.let { inputFormat.parse(it) }
                                val formattedEndTime = endTime?.let { outputFormat.format(it) }
                                binding.permissionOuttime.text = formattedEndTime
//                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    })

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }


        retrofitService = RetrofitService.getInstance()

        sweetAlertDialog = SweetAlertDialog(
            activity,
            SweetAlertDialog.PROGRESS_TYPE
        )
        sweetAlertDialog.setTitleText("Loading")
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getPermissionStatusLiveData().observe(viewLifecycleOwner, Observer {
            try {
                if(sweetAlertDialog.isShowing){
                    sweetAlertDialog.dismiss()
                }

                if (it.data!!.atStatus == "You must Punch In") {
                    binding.punchLayout.isClickable = false
                    binding.punchLayout.visibility = View.GONE
                    binding.punchToday.text = "You must Punch In"
                    binding.noCard.visibility = View.VISIBLE
                    binding.inOutTimeLayout.visibility = View.GONE
                } else if (it.data.atStatus == "Not Started") {
                    binding.punchLayout.isClickable = true
                    binding.punchToday.text = "Start Permission"
                    binding.punchLayout.visibility = View.VISIBLE
                    binding.noCard.visibility = View.GONE
                    binding.inOutTimeLayout.visibility = View.VISIBLE
                } else if (it.data.atStatus == "Started") {
                    binding.punchLayout.isClickable = true
                    binding.punchToday.text = "End Permission"
                    binding.punchInBtn.setImageResource(R.drawable.circular_background_red_punch_out)
                    binding.punchLayout.visibility = View.VISIBLE
                    binding.noCard.visibility = View.GONE
                    binding.inOutTimeLayout.visibility = View.VISIBLE
                } else {
                    binding.punchLayout.visibility = View.GONE
                    binding.attendanceTaken.visibility = View.VISIBLE
                    binding.attendanceTaken.text = "Permission taken today"
                    binding.noCard.visibility = View.GONE
                    binding.inOutTimeLayout.visibility = View.VISIBLE
                }


                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val outputFormat = SimpleDateFormat("hh:mm aa", Locale.US)

                val startTime = inputFormat.parse(it.data!!.permissionStartTime)
                val formattedStartTime = startTime?.let { outputFormat.format(it) }
                binding.permissionIntime.text = formattedStartTime

                val endTime = it.data.permissionEndTime.let { inputFormat.parse(it) }
                val formattedEndTime = endTime?.let { outputFormat.format(it) }


                binding.permissionOuttime.text = formattedEndTime

            } catch (e: Exception) {

            }

        })

        viewModel.isLogin(Util.getPreference(requireActivity().applicationContext,"userId","").toString()
            ,retrofitService)
        viewModel.isloginstatus().observe(viewLifecycleOwner, Observer {
            try {


                if (it.data.atStatus == 0) {
                    binding.punchLayout.visibility = View.GONE
                    binding.noCard.visibility = View.VISIBLE
                    binding.inOutTimeLayout.visibility = View.GONE
                    if(sweetAlertDialog.isShowing){
                        sweetAlertDialog.dismiss()
                    }
                } else if (it.data.atStatus == 1) {
                    binding.punchLayout.visibility = View.GONE
                    binding.noCard.visibility = View.VISIBLE
                    binding.inOutTimeLayout.visibility = View.GONE
                    if(sweetAlertDialog.isShowing){
                        sweetAlertDialog.dismiss()
                    }
                } else if (it.data.atStatus == 2) {
                    viewModel.permissionStatus(
                        Util.getPreference(requireActivity().applicationContext, "userId", "").toString(),
                        retrofitService
                    )
                } else {
                    viewModel.permissionStatus(
                        Util.getPreference(requireActivity().applicationContext, "userId", "").toString(),
                        retrofitService
                    )
                }



            } catch (e: Exception) {

            }

        })
        if (!sweetAlertDialog.isShowing) {
            sweetAlertDialog.show()
        }



        return root
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.slings.vasantham.ui.attendance
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener
import com.bumptech.glide.Glide
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.ViewModel.MainViewModel
import com.slings.vasantham.databinding.AttendanceFragmentBinding
import com.slings.vasantham.service.RetrofitService
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceFragment : Fragment() {
    private var _binding: AttendanceFragmentBinding? = null
    private val client = OkHttpClient()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var viewModel: MainViewModel
    lateinit var sweetAlertDialog: SweetAlertDialog
    lateinit var retrofitService:RetrofitService
    lateinit var  spannableString:SpannableString

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        _binding = AttendanceFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        try {
                binding.topbar.attenName.text = "Hi, "+Util.getPreference(requireActivity()
                    .applicationContext,"username","")
                binding.topbar.attenDesgn.text = "Welcomes to Vasantham"
                binding.textViewCurrentTime.text = currentTime
                binding.tvPlace.text = "Chennai"
            Glide.with(this)
                .load(Util.getPreference(requireActivity(),"profileUrl",""))
                .error(com.slings.vasantham.R.drawable.menu_user_profile)
                .into(binding.topbar.imageView)
                // Set shift time with bold text
                val shiftTimeText = "Your Shift time is: "+Util.getPreference(requireActivity()
                    .applicationContext,"shiftname","")
                if((Util.getPreference(requireActivity()
                        .applicationContext,"shiftname",""))!!.equals("Not Yet Allocated")){
                    binding.punchLayout.visibility = View.GONE
                    binding.noCard.visibility = View.VISIBLE
                }
               spannableString = SpannableString(shiftTimeText)
                val boldStyleSpan = StyleSpan(Typeface.BOLD)
                spannableString.setSpan(boldStyleSpan, shiftTimeText.indexOf("is ")+3, shiftTimeText.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                binding.textViewShiftTime.text = spannableString
            } catch (e: Exception) {
                e.printStackTrace()
            }


        binding.punchLayout.setOnClickListener {
            if(binding.punchToday.text.toString() == "Punch In"){
                val intent = Intent(activity, SelfieAttendance::class.java)
                startActivity(intent)
                requireActivity().supportFragmentManager.popBackStack();
            }else{
                try {
                    viewModel.punchOut(Util.getPreference(requireActivity().applicationContext,"userId","").toString()
                        ,retrofitService)
                    if(!sweetAlertDialog.isShowing){
                        sweetAlertDialog.show()
                    }

                    viewModel.getpunchOUtLiveData().observe(viewLifecycleOwner, Observer {
                        if(sweetAlertDialog.isShowing){
                            sweetAlertDialog.dismiss()
                        }
                        try {
                            if (it.success) {
                                val pDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                                pDialog.titleText = "Punched Out Successfully"
                                pDialog.setCancelable(true)
                                pDialog.setConfirmText("OK")
                                    .setConfirmClickListener(OnSweetClickListener { sweetAlertDialog ->
                                        val intent = Intent(activity, MainActivity::class.java)
                                        startActivity(intent)
                                        sweetAlertDialog.dismiss()
                                    })
                                pDialog.show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireActivity()
                                ,
                                "Network error",
                                Toast.LENGTH_LONG
                            ).show()
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
        viewModel.isloginstatus().observe(viewLifecycleOwner, Observer {

            try {
                if(sweetAlertDialog.isShowing){
                        sweetAlertDialog.dismiss()
                    }
                binding.punchToday.text = "Punch In"
                binding.punchLayout.visibility = View.VISIBLE
                binding.noCard.visibility = View.GONE

                    if (it.data.atStatus == 0) {
                        binding.punchLayout.visibility = View.GONE
                        binding.noCard.visibility = View.VISIBLE
                        binding.inOutTimeLayout.visibility = View.GONE
                        binding.tvNocardShiftTime.text = spannableString
                    } else if (it.data.atStatus == 1) {
                        binding.punchToday.text = "Punch In"
                        binding.punchLayout.visibility = View.VISIBLE
                        binding.noCard.visibility = View.GONE
                        binding.inOutTimeLayout.visibility = View.VISIBLE
                    } else if (it.data.atStatus == 2) {
                        binding.punchToday.text = "Punch out"
                        binding.punchInBtn.setImageResource(R.drawable.circular_background_red_punch_out)
                        binding.punchLayout.visibility = View.VISIBLE
                        binding.noCard.visibility = View.GONE
                        binding.inOutTimeLayout.visibility = View.VISIBLE
                    } else {
                        binding.punchLayout.visibility = View.GONE
                        binding.attendanceTaken.visibility = View.VISIBLE
                        binding.attendanceTaken.text = "Already Punched In"
                        binding.noCard.visibility = View.GONE
                        binding.inOutTimeLayout.visibility = View.VISIBLE
                    }

                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    val outputFormat = SimpleDateFormat("hh:mm:aa", Locale.US)

                    val startTime = inputFormat.parse(it.data.punchInTime)
                    val formattedStartTime = startTime?.let { outputFormat.format(it) }
                    binding.attendIntime.text = formattedStartTime
                    val endTime = it.data.punchOutTime?.let { inputFormat.parse(it) }
                    val formattedEndTime = endTime?.let { outputFormat.format(it) }
                    binding.attendOuttime.text = formattedEndTime

            } catch (e: Exception) {

            }

        })

        return root
    }


    override fun onResume() {
        super.onResume()
        viewModel.isLogin(Util.getPreference(requireActivity().applicationContext,"userId","").toString()
                ,retrofitService)
        if(!sweetAlertDialog.isShowing){
            sweetAlertDialog.show()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

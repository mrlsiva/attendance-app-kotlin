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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener
import com.bumptech.glide.Glide
import com.slings.vasantham.Common
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.databinding.AttendanceFragmentBinding
import com.slings.vasantham.ui.gallery.GalleryViewModel
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceFragment : Fragment() {
    private var _binding: AttendanceFragmentBinding? = null
    private val client = OkHttpClient()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        _binding = AttendanceFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.punchInBtn.setImageResource(R.drawable.circular_background);
        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "Loading"
        pDialog.setCancelable(true)
        pDialog.show()
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        try {
            val formBody: RequestBody = FormBody.Builder()
                .add("userId",Util.getPreference(requireActivity().applicationContext,"userId","").toString())
                .add("shiftId", Util.getPreference(requireActivity(),
                    "shiftId","")!!)
                .build()
            val request =
                Request.Builder().url(
                    Common.URL+"api/admin/shifttimewithusers").post(formBody).build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody)
                binding.attenName.text = "Hi, "+Util.getPreference(requireActivity()
                    .applicationContext,"username","")
                binding.attenDesgn.text = "Welcomes to Vasantham"
                binding.textViewCurrentTime.text = currentTime
                binding.tvPlace.text = "Chennai"

                // Set shift time with bold text
                val shiftTimeText = "Your Shift time is: "+Util.getPreference(requireActivity()
                    .applicationContext,"shiftname","")
                if(Util.getPreference(requireActivity()
                        .applicationContext,"shiftname","").equals("Not Yet Allocated")){
                    binding.punchLayout.visibility = View.GONE
                }
                val spannableString = SpannableString(shiftTimeText)
                val boldStyleSpan = StyleSpan(Typeface.BOLD)
                spannableString.setSpan(boldStyleSpan, shiftTimeText.indexOf("is ")+3, shiftTimeText.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                binding.textViewShiftTime.text = spannableString
                pDialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                pDialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.punchLayout.setOnClickListener {
            if(binding.punchToday.text.toString() == "Punch In"){
                val intent = Intent(activity, SelfieAttendance::class.java)
                startActivity(intent)
            }else{
                try {
                    val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
                    pDialog.titleText = "Loading"
                    pDialog.setCancelable(true)
                    pDialog.show()
                    val formBody: RequestBody = FormBody.Builder()
                        .add("userId",Util.getPreference(requireActivity().applicationContext,"userId","").toString())
                        .build()
                    val request =
                        Request.Builder().url(Common.URL+"api/staff/attendance/out"
                        ).post(formBody).build()
                    try {
                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string()
                        val json = JSONObject(responseBody)
                        if (json.has("success")) {
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
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        return root
    }


    override fun onResume() {
        super.onResume()
        try {
            val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.titleText = "Loading"
            pDialog.setCancelable(true)
            pDialog.show()
            val formBody: RequestBody = FormBody.Builder()
                .build()
            val request =
                Request.Builder().url(
                    Common.URL+"api/staff/islogin/" +
                            Util.getPreference(requireActivity().applicationContext, "userId", "")
                                .toString()
                ).post(formBody).build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody)
                val data = json.getJSONObject("data")
               if (data.getInt("atStatus") == 0) {
                    binding.punchLayout.visibility = View.GONE
                } else if (data.getInt("atStatus") == 1) {
                    binding.punchToday.text = "Punch In"
                    binding.punchLayout.visibility = View.VISIBLE
                } else if (data.getInt("atStatus") == 2) {
                    binding.punchToday.text = "Punch out"
                   binding.punchInBtn.setImageResource(R.drawable.circular_background_red)
                    binding.punchLayout.visibility = View.VISIBLE
                } else {
                    binding.punchLayout.visibility = View.GONE
                    binding.attendanceTaken.visibility = View.VISIBLE
                }
                pDialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                pDialog.dismiss()
            }
        }catch (e: Exception) {
                e.printStackTrace()
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

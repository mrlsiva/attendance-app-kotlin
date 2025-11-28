package com.slings.vasantham

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener
import com.slings.vasantham.databinding.ApplyLeaveBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.Double
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ApplyLeaveFrament : Fragment() {
    private var _binding: ApplyLeaveBinding? = null
    private val client = OkHttpClient()
    private val binding get() = _binding!!
    var myCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        _binding = ApplyLeaveBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val myFormat = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.dateEt.setText(dateFormat.format(myCalendar.time))
        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        binding.submitBtn.setOnClickListener {
            if(binding.reasonEd.text.toString().length<5){
                val pDialog2 =
                    SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                pDialog2.titleText = "The reason field must be at least 5 characters."
                pDialog2.setCancelable(true)
                pDialog2.setConfirmText("OK")
                    .setConfirmClickListener(OnSweetClickListener { sweetAlertDialog ->
                        sweetAlertDialog.dismiss()
                    })
                pDialog2.show()
                return@setOnClickListener
            }

            var numeric = true
            try {
                val num = Double.parseDouble(binding.reasonEd.text.toString())
            } catch (e: NumberFormatException) {
                numeric = false
            }

            if (numeric) {
                val pDialog3 =
                    SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                pDialog3.titleText = "Please enter valid reason"
                pDialog3.setCancelable(true)
                pDialog3.setConfirmText("OK")
                    .setConfirmClickListener(OnSweetClickListener { sweetAlertDialog ->
                        sweetAlertDialog.dismiss()
                    })
                pDialog3.show()
                return@setOnClickListener
            }
                try {
                    val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
                    pDialog.titleText = "Loading"
                    pDialog.setCancelable(true)
                    pDialog.setOnShowListener {
                        val formBody: RequestBody = FormBody.Builder()
                            .add("reason", binding.reasonEd.text.toString())
                            .build()
                        val request =
                            Request.Builder().url(
                                Common.URL + "staff/leave/" + Util.getPreference(
                                    requireActivity().applicationContext,
                                    "userId",
                                    ""
                                )
                                    .toString()
                            ).post(formBody).build()
                        try {
                            val response = client.newCall(request).execute()
                            val responseBody = response.body?.string()
                            val json = JSONObject(responseBody)
                            Log.i("responseBody",responseBody.toString());
                            if (json.has("success")) {
                                pDialog.dismiss()
                                val data = json.getJSONObject("data")
                                val pDialog1 =
                                    SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                                pDialog1.titleText = data.getString("message")
                                pDialog1.setCancelable(true)
                                pDialog1.setConfirmText("OK")
                                    .setConfirmClickListener(OnSweetClickListener { sweetAlertDialog ->
                                        sweetAlertDialog.dismiss()
                                        requireActivity().supportFragmentManager.popBackStack();
                                    })
                                pDialog1.show()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            pDialog.dismiss()
                        }
                    }
                    pDialog.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        return root
    }

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.dateEt.setText(dateFormat.format(myCalendar.time))

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

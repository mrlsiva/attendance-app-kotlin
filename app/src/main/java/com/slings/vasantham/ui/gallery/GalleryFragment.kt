package com.slings.vasantham.ui.gallery

import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.slings.vasantham.Common
import com.slings.vasantham.MyAdapter
import com.slings.vasantham.MyPreferences
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.data.model.AttendanceLogResponse
import com.slings.vasantham.databinding.FragmentGalleryBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Load the ArrayList from ShredPreferences
//        val myArrayList = MyPreferences.getArrayList(requireActivity().applicationContext)

        try {
            val formBody: RequestBody = FormBody.Builder()
                .add("userId", Util.getPreference(requireActivity().applicationContext,"userId","").toString())
                .build()
            val request =
                Request.Builder().url(
                    Common.URL+"api/staff/attendance/logs/"+
                        Util.getPreference(requireActivity().applicationContext,"userId","").toString()).post(formBody).build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val gson = Gson()
                val responseBy = gson.fromJson(responseBody, AttendanceLogResponse::class.java)

                val attendanceEntries = responseBy.logs?.data ?: emptyList()
                val adapter = MyAdapter(attendanceEntries)
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Create and set the adapter



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
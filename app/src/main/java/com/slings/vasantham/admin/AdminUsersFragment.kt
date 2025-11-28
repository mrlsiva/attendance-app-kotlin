package com.slings.vasantham.admin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.Common
import com.slings.vasantham.CustomItem
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class AdminUsersFragment : Fragment() {
    private val client = OkHttpClient()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.admin_user_list, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)
        val items: MutableList<CustomItem> = ArrayList()
        try {
//            val formBody: RequestBody = FormBody.Builder()
////                .add("userId", Util.getPreference(requireActivity(),"userId","").toString())
//                .build()
            val request =
                Request.Builder().url(
                    Common.URL+"admin/roles")
                    .get()
                    .build()
            lateinit var responseBody:String
            try {
                val response = client.newCall(request).execute()
                responseBody = response.body?.string().toString()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            val json = JSONObject(responseBody!!)
            if (json.has("success")) {
                val dataArray = json.getJSONObject("data").getJSONArray("data")

                 for (i in 0 until dataArray.length()) {
                     val roleObject = dataArray.getJSONObject(i)

                     val roleId = roleObject.getInt("roleId")
                     val name = roleObject.getString("name")

                     items.add(CustomItem(name,roleId.toString()))
                 }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val toolbar_end_item: ImageView = requireActivity().findViewById(R.id.toolbar_end_item)
        toolbar_end_item.visibility = View.VISIBLE
        toolbar_end_item.setOnClickListener { v: View? ->
            MainActivity.navController.navigate(R.id.nav_create_user_fragment)
        }
        // Add more items as needed

        // Add more items as needed
        val adapter = AdminUserAdapter(requireActivity(), items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
package com.slings.vasantham.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.R
import java.lang.Double.parseDouble


class AdminLeaveFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.leave_fragment, container, false)

        val leaveNoData: CardView = rootView.findViewById(R.id.leave_no_data)
        val leaveData: LinearLayout = rootView.findViewById(R.id.leave_data)

        leaveNoData.setOnClickListener {  v: View? ->
            leaveNoData.visibility = View.GONE
            leaveData.visibility = View.VISIBLE
        }
        leaveData.setOnClickListener { v: View? ->
            leaveNoData.visibility = View.VISIBLE
            leaveData.visibility = View.GONE
        }
        /*  val items: MutableList<CustomItem> = ArrayList()
        items.add(CustomItem("Item 1 Bold", "Item 1 Normal"))
        items.add(CustomItem("Item 2 Bold", "Item 2 Normal"))
        // Add more items as needed

        // Add more items as needed
        val adapter = AdminUsersFragment(requireActivity(), items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)*/
        // Inflate the layout for this fragment
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
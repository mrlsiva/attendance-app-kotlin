package com.slings.vasantham.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.CustomItem
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R


class AdminShiftViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.admin_user_list, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)

        val items: MutableList<CustomItem> = ArrayList()
        items.add(CustomItem("Shift Time", "Morning"))
        items.add(CustomItem("Shift Time", "Night"))
        // Add more items as needed
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Shift View"
        // Add more items as needed
        val adapter = AdminShiftViewAdapter(requireActivity(), items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)


        val toolbar_end_item: ImageView = requireActivity().findViewById(R.id.toolbar_end_item)
        toolbar_end_item.visibility = View.VISIBLE
        toolbar_end_item.setOnClickListener { v: View? ->
       /*     val fragmentB = AdminCreateShiftFragment()
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_main, fragmentB)
            transaction.addToBackStack(null)
            transaction.commit()*/
            MainActivity.navController.navigate(R.id.nav_create_shiftfragment)
        }
        // Inflate the layout for this fragment
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
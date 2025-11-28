package com.slings.vasantham.admin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.CustomItem
import com.slings.vasantham.R
import com.zagori.bottomnavbar.BottomNavBar


class AdminUserDesignFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.admin_user_list, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)

        val items: MutableList<CustomItem> = ArrayList()
        items.add(CustomItem("Ravi", "Admin"))
        items.add(CustomItem("Poorni", "Manager"))
        items.add(CustomItem("Karthick", "Manager"))
        items.add(CustomItem("user2", "Admin"))
        items.add(CustomItem("user list", "Manager"))
        items.add(CustomItem("List user", "Manager"))
        items.add(CustomItem("Gowtham", "Admin"))
        items.add(CustomItem("sharni", "Manager"))
        items.add(CustomItem("Siva", "Manager"))
        // Add more items as needed

        // Add more items as needed
        val adapter = AdminUsDesignAdapter(requireActivity(), items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
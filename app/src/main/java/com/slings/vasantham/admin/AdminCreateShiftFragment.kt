package com.slings.vasantham.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.slings.vasantham.R


class AdminCreateShiftFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.admin_create_shift, container, false)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Create Shift"
       /* val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)

        val items: MutableList<CustomItem> = ArrayList()
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
package com.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.R


class FirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.one, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)

        val items: MutableList<CustomItem> = ArrayList()
        items.add(CustomItem("Item 1 Bold", "Item 1 Normal"))
        items.add(CustomItem("Item 2 Bold", "Item 2 Normal"))
        // Add more items as needed

        // Add more items as needed
        val adapter = oneCustomAdapter(requireActivity(), items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        // Inflate the layout for this fragment
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
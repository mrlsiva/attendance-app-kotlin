package com.slings.vasantham.admin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.zagori.bottomnavbar.BottomNavBar


class AdminCreateUserFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.admin_create_user, container, false)
        val toolbar_end_item: ImageView = requireActivity().findViewById(R.id.toolbar_end_item)
        toolbar_end_item.visibility = View.GONE

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Create User"

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
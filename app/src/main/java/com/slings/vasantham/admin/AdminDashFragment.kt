package com.slings.vasantham.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.api.internal.LifecycleCallback.getFragment
import com.google.android.material.tabs.TabLayout
import com.slings.vasantham.R
import com.slings.vasantham.admin.ViewPagerAdapter
import java.util.Locale

lateinit var adapter: ViewPagerAdapter

class AdminDashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.admin_dashboard, container, false)
        val tabLayout: TabLayout = rootView.findViewById(R.id.tabLayout)
        val viewPager: ViewPager = rootView.findViewById(R.id.viewPager)
        val toolbar_end_item: ImageView = requireActivity().findViewById(R.id.toolbar_end_item)
        toolbar_end_item.visibility = View.GONE
        tabLayout.setupWithViewPager(viewPager)
        setupViewPager(viewPager)
        return rootView
    }

    fun setupViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(getFragment("Profile"),"Profile")
        adapter.addFragment(getFragment("Attendance"),"Attendance")
        adapter.addFragment(getFragment("Leave"),"Leave")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        adapter.notifyDataSetChanged()
    }

    private fun getFragment(fragmentName: String): Fragment {
        val args = Bundle()
        lateinit var fragment: Fragment
        when (fragmentName) {
            "Profile"-> {
//                args.putString(Constants.COURSE, Constants.ON_GOING)
                fragment = AdminUserViewFragment()
                fragment.arguments = args
                return fragment
            }
            "Attendance" -> {
                fragment = AdminAttendanceFragment()
                fragment.arguments = args
                return fragment
            }
            "Leave" -> {
                fragment = AdminLeaveFragment()
                fragment.arguments = args
                return fragment
            }
        }
        return fragment
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
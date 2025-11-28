package com.slings.vasantham.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.slings.vasantham.Common
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.ViewModel.MainViewModel
import com.slings.vasantham.databinding.ProfileBinding
import com.slings.vasantham.service.RetrofitService
import com.slings.vasantham.service.RetrofitService.Companion.retrofitService
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private var _binding: ProfileBinding? = null
    private val client = OkHttpClient()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var viewModel: MainViewModel
    lateinit var sweetAlertDialog: SweetAlertDialog
    lateinit var retrofitService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val roundedButton:Button = root.findViewById<Button>(R.id.roundedButton);
        viewModel = ViewModelProvider(this@ProfileFragment)[MainViewModel::class.java]
        roundedButton.setOnClickListener(View.OnClickListener { view ->
            MainActivity.navController.navigate(R.id.nav_profileeditfragment)
        })
        sweetAlertDialog = SweetAlertDialog(
            activity,
            SweetAlertDialog.PROGRESS_TYPE
        )
        sweetAlertDialog.setTitleText("Loading")
        if (!sweetAlertDialog.isShowing) {
            sweetAlertDialog.show()
        }
        retrofitService = RetrofitService.getInstance()

        viewModel.getprofileLiveData().observe(viewLifecycleOwner, Observer {
            try {
                if (it.success) {
                    Util.setPreference(
                        requireActivity(),"profileUrl",
                        it.data.userImageUrl)
                    requireActivity().runOnUiThread {
                        Glide.with(this)
                            .load(Util.getPreference(requireActivity(),"profileUrl",""))
                            .error(com.slings.vasantham.R.drawable.menu_user_profile)
                            .into(binding.circularImageView)
                    }
                    binding.profileName.text = it.data.firstName+" "+it.data.lastName
                    binding.profileDesign.text = it.data.roleName
                    Util.setPreference(requireActivity(),
                        "username", it.data.firstName+" "+
                                it.data.lastName)
                    Util.setPreference(requireActivity(),
                        "designation",it.data.roleName)
                    binding.profileEmail.text = it.data.email
                    binding.profileMobileNo.text = it.data.mobile
                    binding.profileAddress.text = it.data.address
                    binding.profilePassword.text = "*".repeat(
                        Util.getPreference
                            (requireActivity(),"password","")!!.length)
                    Util.setPreference(requireActivity(),
                        "username", it.data.firstName+" "+it.data.lastName)
                    if(sweetAlertDialog.isShowing){
                        sweetAlertDialog.dismiss()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        try {
            viewModel.profileEditLoad(
                Util.getPreference(requireActivity().applicationContext, "userId", "").toString(),
                retrofitService!!
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
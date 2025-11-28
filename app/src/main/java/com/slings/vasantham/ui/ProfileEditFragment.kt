package com.slings.vasantham.ui

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.slings.vasantham.Common
import com.slings.vasantham.ImageCaptureCropActivity
import com.slings.vasantham.LoginActivity
import com.slings.vasantham.MainActivity
import com.slings.vasantham.R
import com.slings.vasantham.Util
import com.slings.vasantham.ViewModel.MainViewModel
import com.slings.vasantham.databinding.ProfileEditBinding
import com.slings.vasantham.service.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.notify
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileEditFragment : Fragment() {
    private val client = OkHttpClient()
    private var _binding: ProfileEditBinding? = null
    lateinit var dob:String
    lateinit var roldId:String
    lateinit var gender:String
    lateinit var profileUrl:String
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val PICK_IMAGE_REQUEST = 1
//     var destFile:File = File("")
//    lateinit var sourceFile:File
    lateinit var viewModel: MainViewModel
    lateinit var sweetAlertDialog: SweetAlertDialog
lateinit var retrofitService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        _binding = ProfileEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel = ViewModelProvider(this@ProfileEditFragment)[MainViewModel::class.java]
        sweetAlertDialog = SweetAlertDialog(
            activity,
            SweetAlertDialog.PROGRESS_TYPE
        )
        if (!sweetAlertDialog.isShowing) {
            sweetAlertDialog.show()
        }
        retrofitService = RetrofitService.getInstance()
        sweetAlertDialog.setTitleText("Loading")
        viewModel.profileEditLoad(
            Util.getPreference(requireActivity().applicationContext, "userId", "").toString(),
             retrofitService
        )
        viewModel.getprofileLiveData().observe(viewLifecycleOwner, Observer {
            if(sweetAlertDialog.isShowing){
                sweetAlertDialog.dismiss()
            }
            try {
                if (it.success) {
                    binding.fnameEt.setText(
                        it.data.firstName)
                    binding.lastNameEt.setText(
                        it.data.lastName)
                    binding.designEt.setText(
                        it.data.roleName)
                    binding.mailEt.setText(
                        it.data.email)
                    binding.phoneEt.setText(
                        it.data.mobile)
                    binding.addressEt.setText(
                        it.data.address)
                    profileUrl =  it.data.userImageUrl
                    binding.passwordEt.setText(
                        Util.getPreference(requireActivity(),"password",""))
                    binding.confPasswordEt.setText(
                        Util.getPreference(requireActivity(),"password",""))
                    dob =   it.data.userImageUrl
                    roldId = it.data.roleId.toString()
                    gender = it.data.gender
                    Util.setPreference(
                        requireActivity(),"profileUrl",
                        it.data.userImageUrl)
                    requireActivity().runOnUiThread {
                        Glide.with(this)
                            .load(profileUrl)
                            .error(com.slings.vasantham.R.drawable.menu_user_profile)
                            .into(binding.logoImageView);
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireActivity(),
                    "Network error",
                    Toast.LENGTH_LONG
                ).show()
            }
        });


        binding.logoImageView.setOnClickListener(View.OnClickListener {
           val intent = Intent(activity,ImageCaptureCropActivity::class.java)
           startActivity(intent);
        })

        binding.submitBtn.setOnClickListener{
            if(binding!!.passwordEt.text.toString() != binding!!.confPasswordEt.text.toString()){
                Toast.makeText(activity,"Password does not match",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(binding!!.passwordEt.text.toString().length<4){
                Toast.makeText(activity,"Password need atleast 4 characters",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            try {
                requireActivity().runOnUiThread {
                    if (!sweetAlertDialog.isShowing) {
                        sweetAlertDialog.show()
                    }
                }
                GlobalScope.launch {
                    delay(3000)
                    val builder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("firstName", binding.fnameEt.text.toString())
                        .addFormDataPart("lastName", binding.lastNameEt.text.toString())
                        .addFormDataPart("email", binding.mailEt.text.toString())
                        .addFormDataPart("password", binding.passwordEt.text.toString())
                        .addFormDataPart(
                            "password_confirmation",
                            binding.confPasswordEt.text.toString()
                        )
                        .addFormDataPart("mobile", binding.phoneEt.text.toString())
                        .addFormDataPart("gender", gender)
                        .addFormDataPart("DOB", dob)
                        .addFormDataPart("role", roldId)
                        .addFormDataPart("address", binding.addressEt.text.toString())
                    Util.profileUri?.let { it1 ->
                        builder.addFormDataPart(
                            "userImageUrl", Util.getPreference(
                                requireActivity()
                                    .applicationContext, "userId", ""
                            ).toString(),
                            getFileFromUri(
                                requireActivity(),
                                it1
                            )!!.asRequestBody("image/*".toMediaTypeOrNull())
                        )
                    } ?: run {

                    }
                    builder.addFormDataPart("roleName", binding.designEt.text.toString())
                    val formBody: RequestBody = builder.build()
                    val request =
                        Request.Builder().url(
                            Common.URL + "staff/profile/edit/" +
                                    Util.getPreference(
                                        requireActivity()
                                            .applicationContext, "userId",
                                        ""
                                    )
                                        .toString()
                        ).post(formBody).build()
                    try {
                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string()
                        val json = JSONObject(responseBody)
                        sweetAlertDialog.dismiss()
                        if (json.has("message")) {
                            Util.setPreference(
                                requireActivity(),
                                "username", binding!!.fnameEt.text.toString() +
                                        " " + binding!!.lastNameEt.text.toString()
                            )
                            Util.setPreference(
                                requireActivity(),
                                "designation", binding!!.designEt.text.toString()
                            )
                            withContext(Dispatchers.Main) {
                                val pDialog1 =
                                    SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                                if (binding.passwordEt.text.toString() != Util.getPreference(
                                        requireActivity(),
                                        "password",
                                        ""
                                    )
                                ) {
                                    pDialog1.titleText =
                                        json.getString("message") + ". Password Updated. Please login again"
                                } else {
                                    pDialog1.titleText = json.getString("message")
                                }
                                pDialog1.setCancelable(true)
                                pDialog1.setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener { sweetAlertDialog ->
                                        sweetAlertDialog.dismiss()
                                        if (binding.passwordEt.text.toString() != Util.getPreference(
                                                requireActivity(),
                                                "password",
                                                ""
                                            )
                                        ) {
                                            Util.setPreference(requireActivity(), "mobile", "")
                                            Util.setPreference(requireActivity(), "password", "")
                                            Util.setPreference(requireActivity(), "userId", "")
                                            Util.setPreference(requireActivity(), "username", "")
                                            Util.setPreference(requireActivity(), "shiftname", "")
                                            Util.setPreference(requireActivity(), "token", "")
                                            val intent =
                                                Intent(requireActivity(), LoginActivity::class.java)
                                            requireActivity().startActivity(intent)
                                            requireActivity().finish()
                                        } else {
                                            requireActivity().onBackPressed();
                                        }

                                    })
                                pDialog1.show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                val pDialog2 =
                                    SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                                pDialog2.titleText = json.getString("error")
                                pDialog2.setCancelable(true)
                                pDialog2.setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog.OnSweetClickListener { sweetAlertDialog ->
                                        val intent = Intent(activity, MainActivity::class.java)
                                        startActivity(intent)
                                        sweetAlertDialog.dismiss()
                                    })
                                pDialog2.show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        sweetAlertDialog.dismiss()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        // Code to be executed when the fragment is resumed
//    Toast.makeText(activity,"Testing",Toast.LENGTH_LONG).show()
        try{
//                sourceFile = getFileFromUri(requireActivity(), it1)!!
            Util.profileUri?.let { it1 ->
//                destFile = File(requireActivity().getExternalFilesDir(null),"profile.jpg")
////                val maxSizeInBytes:Long = 1024 * 1024
//                resizeAndCompressImage(sourceFile,destFile)
                Glide.with(this)
                    .load(Util.profileUri)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(com.slings.vasantham.R.drawable.menu_user_profile)
                    .into(binding.logoImageView)
            }?: run {
                   Glide.with(this)
                        .load(profileUrl)
                        .error(com.slings.vasantham.R.drawable.menu_user_profile)
                        .into(binding.logoImageView);
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        // Additional onResume logic or operations
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        sourceFile =
//        destFile = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"profile.jpg")
//        val maxSizeInBytes:Long = 1024 * 1024
//        resizeAndCompressImage(sourceFile, destFile,maxSizeInBytes)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                    DocumentsContract.isDocumentUri(context, uri) -> {
                // Handle content URI using DocumentContract (API level 19 and above)
                getFilePathFromDocumentUri(context, uri)
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                // Handle content URI (API level below 19)
                getFilePathFromContentUri(context, uri)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                // Handle file URI
                File(uri.path)
            }
            else -> null
        }
    }


    private fun getFilePathFromDocumentUri(context: Context, uri: Uri): File? {
        when {
            isExternalStorageDocument(uri) -> {
                // Handle external storage documents
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return File(context.getExternalFilesDir(null), split[1])
                }
            }
            isDownloadsDocument(uri) -> {
                // Handle downloads documents
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            }
            isMediaDocument(uri) -> {
                // Handle media documents
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]

                val contentUri = when (type) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }
        return null
    }

    private fun getFilePathFromContentUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val filePath = context.cacheDir.absolutePath + File.separator +
                System.currentTimeMillis().toString() + ".tmp"

        try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(filePath).use { output ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }
            }
            return File(filePath)
        } catch (e: Exception) {
            Log.e("FileUtils", "Error getting file path from content URI", e)
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): File? {
        context.contentResolver.query(uri!!, null, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("_data")
                if (columnIndex != -1) {
                    val filePath = cursor.getString(columnIndex)
                    if (filePath != null) {
                        return File(filePath)
                    }
                }
            }
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}

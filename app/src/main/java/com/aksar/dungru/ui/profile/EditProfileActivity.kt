package com.aksar.dungru.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityEditProfileBinding
import com.aksar.dungru.databinding.DiglogGenderBinding
import com.aksar.dungru.models.request.ProfileReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.USER_DATA
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.aksar.dungru.utils.extensions.isAllFieldAreValidated
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.ProfileViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val REQUEST_PERMISSIONS = 111
    private val PIC_IMAGE_REQUEST_CODE = 222
    private var uri: Uri? = null
    private var data : LoggedUserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[ProfileViewModel::class.java]

        data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)

        setSavedData()
        initObserver()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> onBackPressed()

            binding.btnCamera -> selectPhoto()

            binding.btnSave -> {
                //For image upload
                if (uri != null){
                    val realPath = getRealPathFromUri(this, uri!!)
                    Log.d("onClick45: ",realPath.toString())
                    val imageFile = File(realPath)
                    Log.d( "onClick12345: ",imageFile.toString())
                    val requestFile: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
                    Log.d( "onClick1234: ",requestFile.toString())
                    val image = MultipartBody.Part.createFormData("userfile", imageFile.name, requestFile)
                    Log.d( "onClick123: ",image.toString())
                    val userIdRequestBody = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        data?.user_id.toString()
                    )
                    viewModel.uploadProfileImage(image, userIdRequestBody)
                }else{
                    if(isAllFieldAreValidated(binding.etName, binding.etUniqueName)){
                        viewModel.updateProfile(
                            ProfileReq(
                                data?.unique_token.toString(),
                                data?.user_id.toString(),
                                data?.email.toString(),
                                binding.etUniqueName.text.toString().trim(),
                                binding.etName.text.toString().trim(),
                                binding.txtDob.text.toString().trim(),
                                binding.txtGender.text.toString().trim()
                            )
                        )
                    }

                }
            }

            binding.txtDob -> showDatePickerDialog()

            binding.txtGender -> showGenderDialog()
        }
    }

    private fun setSavedData() {
        val data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)
        binding.etName.setText(data?.username)
        binding.etUniqueName.setText(data?.unique_name)
        binding.etEmail.setText(data?.email)
        binding.txtDob.text = data?.dob
        binding.txtGender.text = data?.gender


        //have to set image url from data
        if (!data?.image.isNullOrBlank()) {
            val imageUrl = Constance.ImageBaseUrl + data?.image
            Glide.with(this).load(imageUrl).centerCrop()
                .placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.user_placeholder)
                .into(binding.profileImage)
        } else binding.profileImage.setImageResource(R.drawable.user_placeholder)
    }

    private fun selectPhoto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val permissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (!Utils(this).isAllPermissionsGranted(permissionList)) {
                Utils(this).requestPermission(permissionList, REQUEST_PERMISSIONS)
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, PIC_IMAGE_REQUEST_CODE)
            }
        } else {
            val permissionList = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            if (!Utils(this).isAllPermissionsGranted(permissionList)) {
                Utils(this).requestPermission(permissionList, REQUEST_PERMISSIONS)
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, PIC_IMAGE_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(intent, PIC_IMAGE_REQUEST_CODE)
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PIC_IMAGE_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    uri = data?.data
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            realPath = it.getString(columnIndex)
        }
        return realPath
    }



    private fun showGenderDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DiglogGenderBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.radioGroup.setOnCheckedChangeListener { _, buttonId ->
            when (buttonId) {
                dialogBinding.btnMale.id -> {
                    binding.txtGender.text = dialogBinding.btnMale.text.toString()
                    dialog.dismiss()
                }

                dialogBinding.btnFemale.id -> {
                    binding.txtGender.text = dialogBinding.btnFemale.text.toString()
                    dialog.dismiss()
                }

                dialogBinding.btnOther.id -> {
                    binding.txtGender.text = dialogBinding.btnOther.text.toString()
                    dialog.dismiss()
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                binding.txtDob.text = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
            },
            year,
            month,
            dayOfMonth
        )
        calendar.add(Calendar.YEAR, -16)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun initObserver() {
        viewModel.profileResponse.observe(this) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnSave.visibility = GONE
                    binding.progressBar.visibility = VISIBLE
                }

                is NetworkResponse.Success -> {
//                    showToast(response.data?.message.toString(), false)
                    onBackPressed()
                }

                is NetworkResponse.Error -> {
                    binding.btnSave.visibility = VISIBLE
                    binding.progressBar.visibility = GONE
                    showToast(response.data?.message.toString(), false)
                }
            }
        }


        viewModel.imageResponse.observe(this) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnSave.visibility = GONE
                    binding.progressBar.visibility = VISIBLE
                }

                is NetworkResponse.Success -> {
//                    showToast(response.data?.message.toString(), false)
                    if(isAllFieldAreValidated(binding.etName, binding.etUniqueName)) {
                        viewModel.updateProfile(
                            ProfileReq(
                                data?.unique_token.toString(),
                                data?.user_id.toString(),
                                data?.email.toString(),
                                binding.etUniqueName.text.toString().trim(),
                                binding.etName.text.toString(),
                                binding.txtDob.text.toString().trim(),
                                binding.txtGender.text.toString().trim()
                            )
                        )
                    }
                }

                is NetworkResponse.Error -> {
                    binding.btnSave.visibility = VISIBLE
                    binding.progressBar.visibility = GONE
                    showToast(response.data?.message.toString(), false)
                }
            }
        }
    }
}
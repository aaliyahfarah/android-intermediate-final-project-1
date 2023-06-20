package com.bangkit.storyappdicoding.ui

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.data.User
import com.bangkit.storyappdicoding.databinding.ActivityAddStoryBinding
import com.bangkit.storyappdicoding.util.UserPreference
import com.bangkit.storyappdicoding.util.createTempFile
import com.bangkit.storyappdicoding.util.reduceFileImage
import com.bangkit.storyappdicoding.util.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStory : AppCompatActivity() {
    companion object {
        private val REQUIRED_PERMISSION = arrayOf(CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var currentPhotoPath: String
    private lateinit var user: User

    private var file: File? = null
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val mFile = File(currentPhotoPath)
            file = mFile
            val result = BitmapFactory.decodeFile(mFile.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            val mFile = uriToFile(selectedImage, this@AddStory)
            file = mFile
            binding.previewImageView.setImageURI(selectedImage)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_story)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION)
        }

        binding.lifecycleOwner = this
    }

    private fun setupViewModel() {
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddStoryViewModel::class.java]

        binding.viewModel = addStoryViewModel

        addStoryViewModel.user.observe(this) {
            user = it
        }

        addStoryViewModel.status.observe(this) {
            if (!it.error) {
                finish()
            }
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddStory,
                "com.bangkit.storyappdicoding",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_photo))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        if (file != null) {
            val mFile = reduceFileImage(file as File)

            val description = binding.descriptionEditText.text.toString()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = mFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                mFile.name,
                requestImageFile
            )

            addStoryViewModel.uploadStory(user.token, description, imageMultipart)
        } else {
            Toast.makeText(this, getString(R.string.upload_warning), Toast.LENGTH_SHORT).show()
        }
    }
}
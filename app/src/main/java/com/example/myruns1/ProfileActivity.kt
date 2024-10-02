package com.example.myruns1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import android.media.ExifInterface
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File

class ProfileActivity : AppCompatActivity() {

    // Initializing Variables
    private lateinit var imageUri: Uri
    private lateinit var button: Button
    private lateinit var imageView: ImageView
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var captureImageLauncher: ActivityResultLauncher<Intent>
    private val imageFileName = "profile_image.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.profileImage)
        button = findViewById(R.id.btnchangephoto)

        // Initialize SharedPreferences for saving user profile data
        sharedPreferences = getSharedPreferences("UserProfileData", Context.MODE_PRIVATE)

        AppUtil.checkPermissions(this)

        // Creating a file URI for storing the captured image
        val imageFile = File(getExternalFilesDir(null), imageFileName)
        imageUri = FileProvider.getUriForFile(
            this, "com.example.myruns1.fileprovider", imageFile
        )

        // Register the camera activity result launcher
        captureImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val rotatedBitmap = rotateImage(imageFile)
                    viewModel.profileImage.value = rotatedBitmap
                }
            }

        button.setOnClickListener {
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            captureImageLauncher.launch(captureIntent)
        }

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel.profileImage.observe(this) { imageView.setImageBitmap(it) }

        // If image file exists, load the image into the ImageView
        if (imageFile.exists()) {
            // Using the rotateImage function to properly set orientation
            val existingImage = rotateImage(imageFile)
            imageView.setImageBitmap(existingImage)
        }

        loadProfileData()
        handleProfileActions()
    }

    // Function to rotate image to correct orientation
    private fun rotateImage(imageFile: File): Bitmap {
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL)

        val rotation = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        return AppUtil.loadBitmapFromUri(this, imageUri, rotation)
    }

    // Function to handle saving data in SharedPreferences
    private fun handleProfileActions() {
        val nameInput: EditText = findViewById(R.id.nameInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val phoneInput: EditText = findViewById(R.id.phoneInput)
        val genderGroup: RadioGroup = findViewById(R.id.genderGroup)
        val majorInput: EditText = findViewById(R.id.majorInput)
        val classInput: EditText = findViewById(R.id.classInput)
        val saveButton: Button = findViewById(R.id.saveButton)
        val cancelButton: Button = findViewById(R.id.cancelButton)

        // Handle Save button click
        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val phone = phoneInput.text.toString()
            val genderId = genderGroup.checkedRadioButtonId
            val gender = if (genderId == R.id.radioMale) "Male" else "Female"
            val classDate = classInput.text.toString()
            val major = majorInput.text.toString()

            // Save data in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("name", name)
            editor.putString("email", email)
            editor.putString("phone", phone)
            editor.putString("gender", gender)
            editor.putString("major", major)
            editor.putString("classDate", classDate)
            editor.apply()
            finish()
        }

        // Handle Cancel button click
        cancelButton.setOnClickListener {
            nameInput.text.clear()
            emailInput.text.clear()
            phoneInput.text.clear()
            majorInput.text.clear()
            classInput.text.clear()

            genderGroup.clearCheck()
            imageView.setImageResource(R.drawable.ic_profile_placeholder_round)

            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            finish()
        }
    }

    // Function to load profile data
    private fun loadProfileData() {
        val nameInput: EditText = findViewById(R.id.nameInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val phoneInput: EditText = findViewById(R.id.phoneInput)
        val genderGroup: RadioGroup = findViewById(R.id.genderGroup)
        val majorInput: EditText = findViewById(R.id.majorInput)
        val classInput: EditText = findViewById(R.id.classInput)

        // Load data from SharedPreferences
        val name = sharedPreferences.getString("name", "")
        val email = sharedPreferences.getString("email", "")
        val phone = sharedPreferences.getString("phone", "")
        val gender = sharedPreferences.getString("gender", "Male")
        val classDate = sharedPreferences.getString("classDate", "")
        val major = sharedPreferences.getString("major", "")

        // Update UI with loaded data
        nameInput.setText(name)
        emailInput.setText(email)
        phoneInput.setText(phone)
        majorInput.setText(major)
        classInput.setText(classDate)
        if (gender == "Male") {
            genderGroup.check(R.id.radioMale)
        } else {
            genderGroup.check(R.id.radioFemale)
        }
    }
}

package com.example.happydog.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.happydog.R
import com.example.happydog.databinding.ActivityAddArticleBinding
import com.example.happydog.getImageUri
import com.example.happydog.mvvm.ChatViewModel
import com.example.happydog.uriToFile
import com.google.firebase.auth.FirebaseAuth

class AddArticleActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddArticleBinding
    lateinit var auth : FirebaseAuth
    lateinit var title: String
    lateinit var article: String
    lateinit var categori: String
    lateinit var img: ImageView
    private var currentImageUri: Uri? = null
    private var getFile: Uri? = null
    private lateinit var fbAuth : FirebaseAuth
    lateinit var vm : ChatViewModel
    private val CAMERA_PERMISSION_REQUEST = 101
    private val STORAGE_PERMISSION_REQUEST = 102

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading...")
        progressDialog.setCancelable(false)

        binding.imgArticle.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        if (checkCameraPermission()) {
                            startCamera()
                        }
                    }
                    options[item] == "Choose from Gallery" -> {
                        if (checkStoragePermission()) {
                            pickImageFromGallery()
                        }
                    }
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }

        binding.btnAdd.setOnClickListener {
            title = binding.etTitle.text.toString()
            article = binding.etArticle.text.toString()
            categori = binding.etKategori.text.toString()
            img = binding.imgArticle
            fbAuth = FirebaseAuth.getInstance()
            val nama = fbAuth.currentUser?.uid.toString()
            vm.getUserr(nama)
            vm.userData.observe(this){
                val namas = it.username
                if(title.isEmpty() || article.isEmpty() || categori.isEmpty() || getFile == null) {
                    Toast.makeText(this, "Pastikan semua bagian artikel telah lengkap!", Toast.LENGTH_LONG).show()
                }
                else {
                    if (namas != null) {
                        progressDialog.show()
                        vm.uploadArticle(title, article, categori, getFile!!, namas)
                    }
                    vm.stMessage.observe(this){
                        Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    }
                    Toast.makeText(this, "Berhasil upload artikel", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun pickImageFromGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Izin baca penyimpanan sudah diberikan, lanjut ke galeri
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
            launchIntentGallery.launch(chooser)
        } else {
            // Minta izin baca penyimpanan
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
            false
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST
            )
            false
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    showToast("Izin kamera diperlukan untuk menggunakan fitur ini")
                }
            }
            STORAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    showToast("Izin baca penyimpanan diperlukan untuk menggunakan fitur ini")
                }
            }
        }
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val selectImg = result.data?.data as Uri
            selectImg.let{ uri->
                val myFile = this?.let { uriToFile(uri, it) }
                getFile = uri
                binding.imgArticle.setImageURI(uri)
            }
        }
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Izin kamera sudah diberikan, lanjut ke kamera
            currentImageUri = this.let { getImageUri(it) }
            launcherIntentCamera.launch(currentImageUri)
        } else {
            // Minta izin kamera
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            getFile = it
            Log.d("Image URI", "showImage: $it")
            binding.imgArticle.setImageURI(it)
        }
    }
}
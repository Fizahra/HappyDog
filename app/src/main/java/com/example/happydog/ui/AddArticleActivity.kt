package com.example.happydog.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.example.happydog.R
import com.example.happydog.databinding.ActivityAddArticleBinding
import com.example.happydog.databinding.ActivityRegisterBinding
import com.example.happydog.getImageUri
import com.example.happydog.mvvm.ChatViewModel
import com.example.happydog.ui.fragment.profile.ProfileViewModel
import com.example.happydog.uriToFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        binding.imgArticle.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        startCamera()
                    }
                    options[item] == "Choose from Gallery" -> {
                        pickImageFromGallery()
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

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launchIntentGallery.launch(chooser)
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
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
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
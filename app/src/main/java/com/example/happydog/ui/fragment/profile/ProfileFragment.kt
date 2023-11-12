package com.example.happydog.ui.fragment.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.example.happydog.R
import com.example.happydog.Utils
import com.example.happydog.adapter.UserAdapter
import com.example.happydog.databinding.FragmentProfileBinding
import com.example.happydog.model.Users
import com.example.happydog.mvvm.ChatViewModel
import com.example.happydog.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProfileFragment : Fragment(){

    private var _binding: FragmentProfileBinding? = null
    private lateinit var fbAuth : FirebaseAuth
    private lateinit var storageRef : StorageReference
    private lateinit var fStore: FirebaseFirestore
    var uri : Uri? = null
    lateinit var bitmap: Bitmap
    lateinit var vm : ChatViewModel
    lateinit var nvm : ProfileViewModel
    lateinit var pd : ProgressDialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nvm=
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fbAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(activity)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)
        nvm = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val user = fbAuth.currentUser
        fStore = Firebase.firestore
        val uid = fbAuth.currentUser?.uid.toString()
        Log.d("uid","ini uid $uid")
        if (uid.isNotEmpty()){
            getUserData()
        }

        nvm.getUser(uid)
        nvm.userData.observe(viewLifecycleOwner){
            binding.tvProfile.text = it.username
            Log.d("uid","ini uidusername${it.username}")
            Glide.with(requireContext()).load(it.imageUrl).placeholder(R.drawable.logo_happyvet).dontAnimate()
                .into(binding.imgProfile)
        }

        binding.button.setOnClickListener{
            logOut()
        }

        binding.btnSave.setOnClickListener {
            vm.updateProfile()
        }

        binding.imgProfile.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        takePhotoWithCamera()
                    }
                    options[item] == "Choose from Gallery" -> {
                        pickImageFromGallery()
                    }
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
    }
     super.onViewCreated(view, savedInstanceState)
    }

    private fun getUserData() {
    }

    private fun logOut(){
        val ad = getActivity()?.let { AlertDialog.Builder(it) }
        ad?.setTitle(getString(R.string.logout_confirm))
            ?.setPositiveButton(getString(R.string.yes)){ _, _ ->
                fbAuth.signOut()
                val intent = Intent(getActivity(), LoginActivity::class.java)
                getActivity()?.startActivity(intent)
                activity?.finish()
            }
            ?.setNegativeButton(getString(R.string.no), null)
        val alert = ad?.create()
        alert?.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickPictureIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    // To take a photo with the camera, you can use this code
    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    uploadImageToFirebaseStorage(imageBitmap)
                }
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        bitmap = imageBitmap!!
        binding.imgProfile.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener {
            val task = it.metadata?.reference?.downloadUrl
            task?.addOnSuccessListener {
                uri = it
                vm.imageUrl.value = uri.toString()
            }
            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }
//    override fun onResume() {
//        super.onResume()
//        vm.imageUrl.observe(viewLifecycleOwner, Observer {
//            loadImage(it)
//        })
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
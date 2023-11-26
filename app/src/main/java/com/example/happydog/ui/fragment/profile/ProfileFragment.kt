package com.example.happydog.ui.fragment.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.happydog.R
import com.example.happydog.Utils
import com.example.happydog.databinding.FragmentProfileBinding
import com.example.happydog.getImageUri
import com.example.happydog.mvvm.ChatViewModel
import com.example.happydog.ui.auth.LoginActivity
import com.example.happydog.uriToFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class ProfileFragment : Fragment(){

    private var _binding: FragmentProfileBinding? = null
    private lateinit var fbAuth : FirebaseAuth
    private lateinit var storageRef : StorageReference
    private lateinit var fStore: FirebaseFirestore
    var uri : Uri? = null
    private var currentImageUri: Uri? = null
    private lateinit var getFile: Uri
    lateinit var bitmap: Bitmap
    lateinit var vm : ChatViewModel
    lateinit var nvm : ProfileViewModel
    lateinit var pd : ProgressDialog
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

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
//        if (!allPermissionsGranted()) {
//            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS.toString())
//        }


        fbAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(activity)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)
        nvm = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val user = fbAuth.currentUser
        fStore = Firebase.firestore
        val uid = fbAuth.currentUser?.uid.toString()
        Log.d("uid","ini uid $uid")

        nvm.getUser(uid)
        nvm.userData.observe(viewLifecycleOwner){
            binding.tvProfile.text = it.username
            Log.d("uid","ini uidusername${it.username}")
            Glide.with(requireContext()).load(it.imageUrl).placeholder(R.drawable.logo_happyvet).dontAnimate()
                .into(binding.imgProfile)
            if (it.role != "user"){
                binding.tvRole.text = it.role
            }

        }

        binding.button.setOnClickListener{
            logOut()
        }

        binding.btnSave.setOnClickListener {
            nvm.uploadPhoto(getFile, uid)
            activity?.let { it1 ->
                nvm.stError.observe(it1){
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_LONG).show()
                }
            }
            activity?.let { it1 ->
                nvm.isLoading.observe(it1) {
                    showLoading(it)
                }
            }
        }

        binding.imgProfile.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
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
     super.onViewCreated(view, savedInstanceState)
    }

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
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

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
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
                val myFile = activity?.let { uriToFile(uri, it) }
                getFile = uri
                binding.imgProfile.setImageURI(uri)
            }
        }
    }

    private fun takePhotoWithCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)
    }


    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == CAMERA_X_RESULT){
            val myFile = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                it.data?.getSerializableExtra("picture", File::class.java)
            }else{
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? Uri

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let{ file ->
//                rotateImage(file, isBackCamera)
                getFile = file
                binding.imgProfile.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
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

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Initialize your camera-related code here
            } else {
                // Permission denied
                // Handle the denial, show a message, or disable camera-related features
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

    private fun startCamera() {
        currentImageUri = getActivity()?.let { getImageUri(it) }
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
            binding.imgProfile.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.pbData.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    private val launchIntentCameras = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == CAMERA_X_RESULT){
            val myFile = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                it.data?.getSerializableExtra("picture", File::class.java)
            }else{
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? Uri

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let{ file ->
//                rotateImage(file, isBackCamera)
                getFile = file
                binding.imgProfile.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    companion object{
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS= 10
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
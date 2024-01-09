package com.example.happydog.ui.fragment.detection

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.happydog.R
import com.example.happydog.databinding.FragmentDetectionBinding
import com.example.happydog.getImageUri
import com.example.happydog.ml.DiseaseModel
import com.example.happydog.uriToBitmap
import com.example.happydog.uriToFile
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DetectionFragment : Fragment() {
    private var _binding: FragmentDetectionBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private var getFile: Uri? = null
    var imageSize = 224
    private val CAMERA_PERMISSION_REQUEST = 101
    private val STORAGE_PERMISSION_REQUEST = 102

    private lateinit var result: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.imgDetect.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Gambar untuk dideteksi")
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

        result = binding.tvResults

        return root
    }

    private fun pickImageFromGallery() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
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
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    private fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
            false
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST
            )
            false
        }
    }


    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == AppCompatActivity.RESULT_OK){
            val selectImg = result.data?.data as Uri
            selectImg.let{ uri->
                val myFile = requireContext().let { uriToFile(uri, it) }
                getFile = uri
                binding.imgDetect.setImageURI(uri)
                val img = uriToBitmap(requireContext(), uri)
                if (img != null) {
                    resultGenerator(img)
                }
            }
        }
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Izin kamera sudah diberikan, lanjut ke kamera
            currentImageUri = activity?.let { getImageUri(it) }
            launcherIntentCamera.launch(currentImageUri)
        } else {
            // Minta izin kamera
            ActivityCompat.requestPermissions(
                requireActivity(),
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
            binding.imgDetect.setImageURI(it)
            val img = uriToBitmap(requireContext(), it)
            if (img != null) {
                resultGenerator(img)
            }
        }
    }

    private fun resultGenerator(imageBitmap: Bitmap){
        try {
            val res = binding.tvResult
            val model = activity?.let { DiseaseModel.newInstance(it) }

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageBitmap.width * imageBitmap.height)
            imageBitmap.getPixels(intValues, 0, imageBitmap.width, 0, 0, imageBitmap.width, imageBitmap.height)
//            imageBitmap.getPixels(intValues, 0, imageBitmap.width, 0, 0, imageBitmap.width, imageBitmap.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++]
                    byteBuffer.putFloat(((`val` shr 16) and 0xFF) * (1f / 255f)) // Red channel
                    byteBuffer.putFloat(((`val` shr 8) and 0xFF) * (1f / 255f))  // Green channel
                    byteBuffer.putFloat(((`val` and 0xFF) * (1f / 255f)))        // Blue channel
                }
            }
            inputFeature0.loadBuffer(byteBuffer)
            val outputs = model?.process(inputFeature0)
            val outputFeature0 = outputs?.outputFeature0AsTensorBuffer

            val confidences = outputFeature0?.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences?.indices!!){
                if (confidences[i] > maxConfidence){
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            val classes = arrayOf("Kutu","Cacing", "Ringworm", "Scabies")
            binding.tvResults.text = classes[maxPos]
            res.visibility = View.VISIBLE

//        var s = ""
//        for (i in classes.indices){
//            s += String.format()
//        }

// Releases model resources if no longer used.
            model.close()
        } catch (e: IOException){

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
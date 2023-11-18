package com.example.happydog.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.happydog.R
import com.example.happydog.createFile
import com.example.happydog.databinding.ActivityCameraBinding
import com.example.happydog.ui.fragment.profile.ProfileFragment

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imgCapt: ImageCapture? = null
    private var camSelect: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureImg.setOnClickListener{
            takePhoto()
        }
        binding.switchCam.setOnClickListener{
            camSelect = if(camSelect.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
    }

    public override fun onResume(){
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun takePhoto(){
        val imageCapture = imgCapt ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback{
                override fun onError(e: ImageCaptureException){
                    Toast.makeText(this@CameraActivity, getString(R.string.failed_get_image), Toast.LENGTH_SHORT).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val intent = Intent()
                    intent.putExtra("picture", photoFile)
                    intent.putExtra("isBackCamera", camSelect == CameraSelector.DEFAULT_BACK_CAMERA)
                    setResult(ProfileFragment.CAMERA_X_RESULT, intent)
                    finish()
                }
            }
        )
    }

    private fun startCamera(){
        val camProviderFuture = ProcessCameraProvider.getInstance(this)

        camProviderFuture.addListener({
            val cameraProv: ProcessCameraProvider = camProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also{
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imgCapt = ImageCapture.Builder().build()
            try{
                cameraProv.unbindAll()
                cameraProv.bindToLifecycle(
                    this,
                    camSelect,
                    preview,
                    imgCapt
                )
            }catch(e: Exception){
                Toast.makeText(this@CameraActivity, getString(R.string.failed_start_cam), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun hideSystemUI(){
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}
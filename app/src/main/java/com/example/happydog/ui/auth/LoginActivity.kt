@file:Suppress("DEPRECATION")

package com.example.happydog.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.happydog.R
import com.example.happydog.databinding.ActivityLoginBinding
import com.example.happydog.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginActivity : AppCompatActivity() {

    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit private var fbauth: FirebaseAuth
    lateinit private var pds: ProgressDialog

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fbauth = FirebaseAuth.getInstance()
        setEnable()
        playAnimation()


        pds = ProgressDialog(this)

        binding.etEmail.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    binding.etEmail.error = resources.getString(R.string.error_email)
                }
                setEnable()

            }
        })

        binding.etPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().length < 8) {
                    binding.etPassword.error = resources.getString(R.string.error_password)
                }
                setEnable()
            }
        })

        binding.tvRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()

            if (binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                signIn(password, email)
            }
        }
    }

    private fun signIn(password : String, email : String){
        pds.show()
        pds.setMessage("Signing In")

        fbauth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                pds.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                pds.dismiss()
                Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {exception->
            when (exception){
                is FirebaseAuthInvalidCredentialsException ->{
                    Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
                else-> {
                    // other exceptions
                    Toast.makeText(applicationContext, "Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView2, View.TRANSLATION_X, -30f, 30f).apply{
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val editemail = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1F).setDuration(150)
        val editpw = ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1F).setDuration(150)
        val button = ObjectAnimator.ofFloat(binding.button, View.ALPHA, 1F).setDuration(300)
        val tv = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1F).setDuration(300)
        val tvregis = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1F).setDuration(300)

        AnimatorSet().apply{
            playSequentially(editemail, editpw, button, tv, tvregis)
            startDelay=300
        }.start()
    }

    private fun setEnable(){
        binding.button.isEnabled = binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        finishAffinity()
    }

}
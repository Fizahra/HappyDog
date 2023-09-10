@file:Suppress("DEPRECATION")

package com.example.happydog.ui.auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        pds = ProgressDialog(this)

        binding.tvRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()

            if (binding.etEmail.text.isEmpty()){
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            }
            if (binding.etPassword.text.isEmpty()){
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            }
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
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        pds.dismiss()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        pds.dismiss()

    }
}
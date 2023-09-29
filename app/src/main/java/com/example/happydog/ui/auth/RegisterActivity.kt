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
import com.example.happydog.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var pd : ProgressDialog
    lateinit var auth : FirebaseAuth
    lateinit var firestore : FirebaseFirestore
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit var nomor: String
    private var isError = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setEnable()
        playAnimation()


        pd = ProgressDialog(this)

        binding.tvLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.etEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().isNullOrEmpty()) {
                    binding.etEmail.error = resources.getString(R.string.empty)
                    isError = true
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    binding.etEmail.error = resources.getString(R.string.error_email)
                    isError = true
                }
                else isError = false
                setEnable()

            }
        })

        binding.etPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().isNullOrEmpty()) {
                    binding.etPassword.error = resources.getString(R.string.empty)
                    isError = true
                }
                if(s.toString().length < 8) {
                    binding.etPassword.error = resources.getString(R.string.error_password)
                    isError = true
                }
                else isError = false
                setEnable()

            }
        })

        binding.etNama.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().isNullOrEmpty()) {
                    binding.etNama.error = resources.getString(R.string.empty)
                    isError = true
                }
                else isError = false
                setEnable()

            }
        })

        binding.etNomor.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().isNullOrEmpty()) {
                    binding.etPassword.error = resources.getString(R.string.empty)
                    isError = true
                }
                else isError = false
                setEnable()
            }
        })


        binding.button.setOnClickListener {
            name = binding.etNama.text.toString()
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()
            nomor = binding.etNomor.text.toString()
            if (binding.etNomor.text.isNotEmpty() && binding.etNama.text.isNotEmpty() && binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                createUser(name, password, email, nomor)
            }
        }
    }

    private fun createUser(name : String, password : String, email : String, nomor : String){
        pd.show()
        pd.setMessage("Registering User")

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
            if (task.isSuccessful){
                val user = auth.currentUser
                val dataHashMap = hashMapOf("userid" to user!!.uid!!, "username" to name, "useremail" to email, "status" to "default",
                    "imageUrl" to "https://github.com/Fizahra/HappyDog/blob/master/app/src/main/res/drawable/logo_happyvet.png", "usernomor" to nomor, "role" to "user")

                firestore.collection("Users").document(user.uid).set(dataHashMap)

                pd.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun setEnable(){
        binding.button.isEnabled = isError != true
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView2, View.TRANSLATION_X, -30f, 30f).apply{
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val editname = ObjectAnimator.ofFloat(binding.etNama, View.ALPHA, 1F).setDuration(150)
        val editnomor = ObjectAnimator.ofFloat(binding.etNomor, View.ALPHA, 1F).setDuration(150)
        val editemail = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1F).setDuration(150)
        val editpaw = ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1F).setDuration(150)
        val button = ObjectAnimator.ofFloat(binding.button, View.ALPHA, 1F).setDuration(300)
        val tv = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1F).setDuration(300)
        val tvlogin = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1F).setDuration(300)

        AnimatorSet().apply{
            playSequentially(editname, editnomor, editemail, editpaw, button, tv, tvlogin)
            startDelay = 300
        }.start()
    }
}
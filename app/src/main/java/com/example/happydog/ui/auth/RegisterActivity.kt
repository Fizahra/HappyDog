@file:Suppress("DEPRECATION")

package com.example.happydog.ui.auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        pd = ProgressDialog(this)

        binding.tvLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            name = binding.etNama.text.toString()
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()
            nomor = binding.etNomor.text.toString()

            if (binding.etNama.text.isEmpty()){
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            }
            if (binding.etEmail.text.isEmpty()){
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            }
            if (binding.etPassword.text.isEmpty()){
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            }
            if (binding.etNomor.text.isEmpty()){
                Toast.makeText(this, "Enter Nomor", Toast.LENGTH_SHORT).show()
            }
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
                    "imageUrl" to "https://github.com/Fizahra/HappyDog/blob/master/app/src/main/res/drawable/logo_happyvet.png", "usernomor" to nomor)

                firestore.collection("Users").document(user.uid).set(dataHashMap)

                pd.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}
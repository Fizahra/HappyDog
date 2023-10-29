package com.example.happydog.ui.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.happydog.R
import com.example.happydog.Utils
import com.example.happydog.databinding.ActivityChatBinding
import com.example.happydog.model.Users
import com.example.happydog.mvvm.ChatViewModel
import com.example.happydog.ui.fragment.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    lateinit var vm : ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val users = intent.getParcelableExtra<Users>("UserId")!!
        val nama = users.username
//        users = intent.getParcelableExtra("UserId")
        Log.d("users","ini username $nama")
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        binding.tvName.text = nama
        binding.tvStatus.text = users.status
        Glide.with(this).load(users.imageUrl).placeholder(R.drawable.logo_happyvet).into(binding.imgUser)
        binding.imgBack.setOnClickListener{
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
        }
        binding.btnSend.setOnClickListener{
            vm.sendMessage(Utils.getUidLoggedIn(), users.userid!!, users.username!!, users.imageUrl!! )
        }
        val firebase = FirebaseAuth.getInstance().currentUser
//        val reference = FirebaseFirestore.getInstance().document()
//            FirebaseDatabase.getInstance().getReference("users").child(users)
    }
}
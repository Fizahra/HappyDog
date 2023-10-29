package com.example.happydog.mvvm

import androidx.lifecycle.LiveData
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happydog.MyApp
import com.example.happydog.SharedPrefs
import com.example.happydog.Utils
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*

class ChatViewModel : ViewModel() {
    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val role = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()

    init {
//        getCurrentUser()
    }

    fun getUser(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }

//    private fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
//
//        val context = MyApp.instance.applicationContext
//
//        firestore.collection("Users").document(Utils.getUidLoggedIn())
//            .addSnapshotListener { value, error ->
//
//                if (value!!.exists()) {
//                    val users = value.toObject(Users::class.java)
//                    name.value = users?.username!!
//                    imageUrl.value = users.imageUrl!!
//
//
//                    val mysharedPrefs = SharedPrefs(context)
//                    mysharedPrefs.setValue("username", users.username!!)
//                }
//            }
//    }

}
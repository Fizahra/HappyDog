package com.example.happydog.ui.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    val userData = MutableLiveData<Users>()
    private var fStore: FirebaseFirestore = Firebase.firestore

    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }
    val text: LiveData<String> = _text

    fun getUser(uid: String){
        fStore.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                userData.value = it.toObject<Users>()
            }
    }
}
package com.example.happydog.ui.fragment.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ArcticleViewModel : ViewModel() {
    val userData = MutableLiveData<Users>()
    private var fStore: FirebaseFirestore = Firebase.firestore

    fun getUser(uid: String){
        fStore.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                userData.value = it.toObject<Users>()
            }
    }
}
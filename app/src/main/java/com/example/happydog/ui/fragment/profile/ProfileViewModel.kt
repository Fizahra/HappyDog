package com.example.happydog.ui.fragment.profile

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import android.content.Context


class ProfileViewModel : ViewModel() {
    val userData = MutableLiveData<Users>()
    private var fStore: FirebaseFirestore = Firebase.firestore
    private var storage: FirebaseStorage = Firebase.storage
    val stError = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    val isAdmin = MutableLiveData<String>()

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

    fun uploadPhoto(uri: Uri, uid: String){
        storage.reference.child("Users").child(uid).putFile(uri).addOnSuccessListener {
            storage.reference.child("Users").child(uid).downloadUrl.addOnSuccessListener {
                fStore.collection("Users").document(uid).update("imageUrl", it.toString())
                    .addOnSuccessListener {
                        _isLoading.value = false
                        stError.value = "Success update"
                }.addOnFailureListener {
                        _isLoading.value = false
                    stError.value = "Error: ${it.message}"
                }
            }
        }

    }

}
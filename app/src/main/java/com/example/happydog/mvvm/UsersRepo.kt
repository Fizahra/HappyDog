package com.example.happydog.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.happydog.Utils
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class UsersRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getUsers(): LiveData<List<Users>>{
        val users = MutableLiveData<List<Users>>()

        firestore.collection("Users").addSnapshotListener{snapshot, exception->
            if(exception!=null){
                return@addSnapshotListener
            }

            val userList = mutableListOf<Users>()
            snapshot?.documents?.forEach{document->
                val user = document.toObject(Users::class.java)
                //inigasi?
                if (user!!.userid != Utils.getUidLoggedIn() && user.role != "user"){
                    user.let {
                        userList.add(it)
                    }
                }

                users.value = userList
            }
        }
        return users
    }

}
package com.example.happydog

import com.google.firebase.auth.FirebaseAuth

class Utils {
    companion object{
        private val auth = FirebaseAuth.getInstance()
        private var userid : String = ""

        fun getUidLoggedIn(): String {

            if (auth.currentUser!=null){
                userid = auth.currentUser!!.uid
            }
            return userid
        }

    }
}
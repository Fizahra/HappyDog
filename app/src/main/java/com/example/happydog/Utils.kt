package com.example.happydog

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date

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

        fun getTime(): String {


            val formatter = SimpleDateFormat("HH:mm:ss")
            val date: Date = Date(System.currentTimeMillis())
            val stringdate = formatter.format(date)


            return stringdate

        }

    }
}
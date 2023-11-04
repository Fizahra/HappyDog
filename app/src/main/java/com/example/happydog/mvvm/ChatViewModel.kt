package com.example.happydog.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happydog.MyApp
import com.example.happydog.SharedPrefs
import com.example.happydog.Utils
import com.example.happydog.model.Messages
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*

class ChatViewModel : ViewModel() {
//    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val role = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()
    val msgRepo = MessageRepo()

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

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String, message: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val context = MyApp.instance.applicationContext

            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")

            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener {

                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(setHashap)

                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .update(
                            "message",
                            message,
                            "time",
                            Utils.getTime(),
                            "person",
                            name
                        )
                }
        }

    fun getMessages(friend: String): LiveData<List<Messages>> {
        return msgRepo.getMessages(friend)
    }

}
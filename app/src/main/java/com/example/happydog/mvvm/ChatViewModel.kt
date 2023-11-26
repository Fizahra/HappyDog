package com.example.happydog.mvvm

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happydog.MyApp
import com.example.happydog.SharedPrefs
import com.example.happydog.Utils
import com.example.happydog.model.Articles
import com.example.happydog.model.Messages
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*

class ChatViewModel : ViewModel() {
//    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    private var fStore: FirebaseFirestore = Firebase.firestore
    private var storage: FirebaseStorage = Firebase.storage
    val role = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()
    var stMessage = MutableLiveData<String>()
    val userData = MutableLiveData<Users>()

    val usersRepo = UsersRepo()
    val msgRepo = MessageRepo()
    val artRepo = ArticleRepo()

    init {
//        getCurrentUser()
    }

    fun getUser(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }

    fun getArticle(): LiveData<List<Articles>>{
        return artRepo.getArticles()
    }
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

    fun uploadArticle(title: String, artikel: String, kategori: String, uri: Uri){
        storage.getReference("Articles/$title.jpg")
            .putFile(uri)
            .addOnSuccessListener {
                storage.getReference("Articles/$title.jpg").downloadUrl.addOnSuccessListener {
                    val image = it.toString()
                    val article= hashMapOf(
                        "title" to title,
                        "article" to artikel,
                        "category" to kategori,
                        "date" to Utils.getDate(),
                        "author" to Utils.getUserLoggedIn(),
                        "imageArticle" to image
                    )
                    fStore.collection("Articles")
                        .document()
                        .set(article)
                        .addOnSuccessListener {
                            stMessage.value = "Berhasil menambahkan artikel"
                        }
                }

            }
            .addOnFailureListener {
                stMessage.value = it.message.toString()
            }
    }


//    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
//        val context = MyApp.instance.applicationContext
//        val hashMapUser =
//            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)
//        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser)
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
    val context = MyApp.instance.applicationContext
    val nameValue = name.value
    val imageUrlValue = imageUrl.value

    if (nameValue != null && imageUrlValue != null) {
        val hashMapUser = hashMapOf<String, Any>("username" to nameValue, "imageUrl" to imageUrlValue)

        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
    } else {
        // Handle the case where name.value or imageUrl.value is null
        Toast.makeText(context, "Name or image URL is null", Toast.LENGTH_SHORT).show()
    }
}

}

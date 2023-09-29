package com.example.happydog.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.happydog.model.Users

class ChatViewModel : ViewModel() {
    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()

    val usersRepo = UsersRepo()

    fun getUser(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }
}
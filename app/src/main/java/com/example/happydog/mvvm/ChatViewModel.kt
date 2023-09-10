package com.example.happydog.mvvm

import androidx.lifecycle.MutableLiveData

class ChatViewModel {
    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
}
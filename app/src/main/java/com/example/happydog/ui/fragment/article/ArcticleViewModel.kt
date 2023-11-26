package com.example.happydog.ui.fragment.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.happydog.model.Users

class ArcticleViewModel : ViewModel() {
    val userData = MutableLiveData<Users>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}
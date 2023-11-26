package com.example.happydog.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.happydog.Utils
import com.example.happydog.model.Articles
import com.example.happydog.model.Users
import com.google.firebase.firestore.FirebaseFirestore

class ArticleRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getArticles(): LiveData<List<Articles>> {
        val articles = MutableLiveData<List<Articles>>()

        firestore.collection("Articles").addSnapshotListener{snapshot, exception->
            if(exception!=null){
                return@addSnapshotListener
            }

            val articleList = mutableListOf<Articles>()
            snapshot?.documents?.forEach{document->
                val article = document.toObject(Articles::class.java)
                if (article != null) {
                    articleList.add(article)
                }

                articles.value = articleList
            }
        }
        return articles
    }
}
package com.example.happydog.ui.detailarticle

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.happydog.R
import com.example.happydog.databinding.ActivityDetailArticleBinding
import com.example.happydog.model.Articles
import com.example.happydog.mvvm.ChatViewModel

class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding
    lateinit var vm : ChatViewModel
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val articles = intent.getParcelableExtra<Articles>("ArticleId")!!

        vm = ViewModelProvider(this).get(ChatViewModel::class.java)
        binding.tvTitle.text = articles.title
        binding.tvAuthor.text = "Oleh ${articles.author}"
        binding.tvArticle.text = articles.article
        binding.tvDate.text = articles.date
        Glide.with(this).load(articles.imageArticle).placeholder(R.drawable.logo_happyvet).into(binding.imgArticle)


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Ini akan mensimulasikan tombol kembali
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
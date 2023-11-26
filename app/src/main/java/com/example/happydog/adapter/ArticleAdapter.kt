package com.example.happydog.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.happydog.R
import com.example.happydog.model.Articles
import com.example.happydog.model.Users
import com.example.happydog.ui.chat.ChatActivity
import com.example.happydog.ui.detailarticle.DetailArticleActivity

class ArticleAdapter: RecyclerView.Adapter<ArticleAdapter.ArticleHolder>() {

    private var listArticles= listOf<Articles>()
    class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val category: TextView = itemView.findViewById(R.id.tv_kategori)
        val imgArticle: ImageView = itemView.findViewById(R.id.img_article)
        val date: TextView = itemView.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ArticleHolder(view)
    }

    override fun getItemCount(): Int = listArticles.size

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val articles = listArticles[position]
        val title = articles.title
        val kategori = articles.category
        val date = articles.date

        holder.title.setText(title)
        holder.category.setText(kategori)
        holder.date.setText(date)
        Glide.with(holder.itemView.context).load(articles.imageArticle).placeholder(R.drawable.logo_happyvet).into(holder.imgArticle)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailArticleActivity::class.java)
            intent.putExtra("ArticleId", articles)
            holder.itemView.context.startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Articles>){
        this.listArticles = list
        notifyDataSetChanged()
    }
}
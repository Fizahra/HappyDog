package com.example.happydog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.happydog.R
import com.example.happydog.Utils
import com.example.happydog.model.Messages

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    private var listOfMessage = listOf<Messages>()
    private val LEFT = 0
    private val RIGHT = 1
    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView.rootView) {
        val messageText: TextView = itemView.findViewById(R.id.tv_chat)
        val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
        val img : ImageView = itemView.findViewById(R.id.img_chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == RIGHT) {
            val view = inflater.inflate(R.layout.item_chat_kanan, parent, false)
            MessageHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_kiri, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount(): Int = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]
        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE
        holder.messageText.text = message.message
        holder.timeOfSent.text = message.time?.substring(0, 5) ?: ""
        Glide.with(holder.itemView.context).load(message.imgUrl).placeholder(R.drawable.logo_happyvet).into(holder.img)
    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == Utils.getUidLoggedIn()) RIGHT else LEFT
    fun setList(newList: List<Messages>) {
        this.listOfMessage = newList
    }
}
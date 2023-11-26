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
import com.example.happydog.model.Users
import com.example.happydog.ui.chat.ChatActivity

class UserAdapter: RecyclerView.Adapter<UserAdapter.UserHolder>(){

    private var listUsers = listOf<Users>()
    class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tv_name)
        val role: TextView = itemView.findViewById(R.id.tv_role)
        val img: ImageView = itemView.findViewById(R.id.img_chat)
        val stat: ImageView = itemView.findViewById(R.id.statusOnline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int = listUsers.size

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val users = listUsers[position]

        val name = users.username!!.split("\\s".toRegex())[0]
        holder.username.setText(name)

        if (users.status.equals("Online")){
            holder.stat.setImageResource(R.drawable.onlinestatus)
        } else {
            holder.stat.setImageResource(R.drawable.offlinestatus)
        }

        val role = users.role
        holder.role.setText(role)
        Glide.with(holder.itemView.context).load(users.imageUrl).placeholder(R.drawable.logo_happyvet).into(holder.img)
        holder.itemView.setOnClickListener {
//            listener?.onUserSelected(position, users)
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("UserId", users)
            holder.itemView.context.startActivity(intent)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Users>){
        this.listUsers = list
        notifyDataSetChanged()
    }

//    fun setOnClickListener(listener: OnItemClickListener){
//        this.listener = listener
//    }
//
//    interface OnItemClickListener{
//        fun onUserSelected(position: Int, users: Users)
//    }
}


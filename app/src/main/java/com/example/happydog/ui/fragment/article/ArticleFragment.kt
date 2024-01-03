package com.example.happydog.ui.fragment.article

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happydog.adapter.ArticleAdapter
import com.example.happydog.adapter.UserAdapter
import com.example.happydog.databinding.FragmentArticleBinding
import com.example.happydog.mvvm.ChatViewModel
import com.example.happydog.ui.AddArticleActivity
import com.example.happydog.ui.auth.LoginActivity
import com.example.happydog.ui.fragment.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    lateinit var rv : RecyclerView
    lateinit var adapter: ArticleAdapter
    private lateinit var fbAuth : FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vm =
            ViewModelProvider(this).get(ChatViewModel::class.java)
        val nvm = ViewModelProvider(this).get(ProfileViewModel::class.java)

            _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fbAuth = FirebaseAuth.getInstance()
        val user = fbAuth.currentUser?.uid.toString()
        nvm.getUser(user)
        nvm.userData.observe(viewLifecycleOwner){
            if (it.role != "user"){
                binding.fabAddArticle.visibility = View.VISIBLE
            }else{
                binding.fabAddArticle.visibility = View.GONE
            }
        }
//        vm.userData.observe(viewLifecycleOwner) { userData ->
//            Log.d("ChatViewModel", "UserData observed: $userData")
//            if (userData.role != "user") {
//                Log.d("ChatViewModel", "Role is not 'user'. Showing FloatingActionButton.")
//                binding.fabAddArticle.visibility = View.VISIBLE
//            } else {
//                Log.d("ChatViewModel", "Role is 'user'. Hiding FloatingActionButton.")
//                binding.fabAddArticle.visibility = View.GONE
//            }
//        }

        binding.fabAddArticle.setOnClickListener{
            val intent = Intent(getActivity(), AddArticleActivity::class.java)
            getActivity()?.startActivity(intent)
        }

        adapter = ArticleAdapter()
        rv = binding.rvArticle
        rv.layoutManager = LinearLayoutManager(activity)
        rv.setHasFixedSize(true)

        vm.getArticle().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            rv.adapter = adapter

        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
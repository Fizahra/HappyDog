package com.example.happydog.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happydog.R
import com.example.happydog.adapter.UserAdapter
import com.example.happydog.databinding.FragmentHomeBinding
import com.example.happydog.mvvm.ChatViewModel

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    lateinit var rv : RecyclerView
    lateinit var adapter: UserAdapter
    lateinit var vm : ChatViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)
        adapter = UserAdapter()
        rv = binding.rvListDoc
        rv.layoutManager = LinearLayoutManager(activity)
        rv.setHasFixedSize(true)

        vm.getUser().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            rv.adapter = adapter

        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.example.happydog.ui.fragment.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.happydog.R
import com.example.happydog.adapter.UserAdapter
import com.example.happydog.databinding.FragmentProfileBinding
import com.example.happydog.model.Users
import com.example.happydog.mvvm.ChatViewModel
import com.example.happydog.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(){

    private var _binding: FragmentProfileBinding? = null
    private lateinit var fbAuth : FirebaseAuth
    lateinit var vm : ChatViewModel
    lateinit var pd : ProgressDialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fbAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(activity)
        vm = ViewModelProvider(this).get(ChatViewModel::class.java)

        binding.button.setOnClickListener{
            logOut()
        }
        vm.imageUrl.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(binding.imgProfile)
        })

        return root
    }

    private fun logOut(){
        val ad = getActivity()?.let { AlertDialog.Builder(it) }
        ad?.setTitle(getString(R.string.logout_confirm))
            ?.setPositiveButton(getString(R.string.yes)){ _, _ ->
                fbAuth.signOut()
                val intent = Intent(getActivity(), LoginActivity::class.java)
                getActivity()?.startActivity(intent)
                activity?.finish()
            }
            ?.setNegativeButton(getString(R.string.no), null)
        val alert = ad?.create()
        alert?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
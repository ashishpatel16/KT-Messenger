package com.ashish.messenger.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.ashish.messenger.R
import com.ashish.messenger.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,
            R.layout.fragment_login,
            container,
            false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        binding.buttonNext.setOnClickListener{view->
            view.findNavController().navigate(R.id.action_loginFragment_to_chatsFragment)

        }
        return binding.root
    }
}
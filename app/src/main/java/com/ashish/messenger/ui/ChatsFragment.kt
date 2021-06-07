package com.ashish.messenger.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.ashish.messenger.R
import com.ashish.messenger.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentChatsBinding>(inflater,
            R.layout.fragment_chats,
            container,
            false)
        binding.buttonNext.setOnClickListener{view->
            view.findNavController().navigate(R.id.action_chatsFragment_to_chatFragment)

        }
        return binding.root
    }
}
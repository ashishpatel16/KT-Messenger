package com.ashish.messenger.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ashish.messenger.R
import com.ashish.messenger.databinding.FragmentChatsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Message(val id: String="", val text: String="", val author: String="", val time_posted: String="", val media: String="",val isSender: Boolean=true)

class ChatsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentChatsBinding>(inflater,
            R.layout.fragment_chats,
            container,
            false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        sendMessage("Hi I am noob")

        return binding.root
    }


    private fun sendMessage(text: String) {
        val message = Message("1Lrid5kaZxeWtxJjcwDLoPWL6sd2",text,"Ashish",isSender = false)
        val id = "1Lrid5kaZxeWtxJjcwDLoPWL6sd2"
        db.collection("message")
            .document(id)
            .set(message, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(TAG, "sendMessage: Sent a message! ")
            }
    }
companion object{
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    const val TAG = "ChatsFragment"
}
}
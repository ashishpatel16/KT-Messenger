package com.ashish.messenger.ui

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.ashish.messenger.adapter.ChatAdapter
import com.ashish.messenger.data.Conversation
import com.ashish.messenger.databinding.FragmentChatBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import com.ashish.messenger.Utils.getCurrentTime

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactId: String
    private var conversationId: String? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentChatBinding>(inflater,R.layout.fragment_chat,container,false)


        recyclerView = binding.chatRecyclerview
        contactId = arguments?.get("contactId").toString()
        db = Firebase.firestore

        getConversationId()

        binding.addMediaButton.setOnClickListener{
            var galleryIntent = Intent()
            galleryIntent.apply {
                setType("image/*")
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
                setAction(Intent.ACTION_GET_CONTENT)
            }
            startActivityForResult(galleryIntent, CHOOSE_IMAGE_CODE)
        }
        populateList()


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            if(data?.clipData == null) {
                Log.i(TAG, "onActivityResult: Image Uri captured : " + data!!.data.toString())
            }
        }
    }

    private fun getConversationId() {
        val conversationObj: Conversation
        // Getting the conversation Id

        db.collection("conversations")
                .whereEqualTo("type",0)
                .whereArrayContainsAny("members",listOf(UID,contactId))
                .get()
                .addOnSuccessListener {
                    if(!it.isEmpty) {
                        conversationId = it.documents[0].id.toString()
                        Log.i(TAG, "loadMessages: Found conversation: ${conversationId}")
                    }else {
                        initNewConversation()
                        Log.i(TAG, "getConversationId: Convo ID null")
                    }
                }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initNewConversation() {
        var conversation : Conversation
        val currentDate = getCurrentTime()
        Log.i(TAG, "initNewConversation: $currentDate")
        conversation = Conversation(null,currentDate,UID, listOf(UID,contactId) as List<String>,null,0)
        db.collection("conversations")
                .add(conversation)
                .addOnSuccessListener {
                    Log.i(TAG, "initNewConversation: Started Conversation!")
                }
    }

    private fun initRecyclerView(mList: MutableList<Message>) {

        recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            adapter = ChatAdapter(mList,context)
        }
    }

    private fun populateList():MutableList<Message> {
        val mList = mutableListOf<Message>()
        val url = "https://firebasestorage.googleapis.com/v0/b/ktmessenger-daf3e.appspot.com/o/profile_pictures%2F4d20319a-b31f-41e7-b196-8e9ee1672fa0.png?alt=media&token=ac88608a-c9b1-4e33-9c9d-3022e43a317b"
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        mList.add(Message(url,"Hello World! This is a nice app!!!",isSender = false))
        mList.add(Message(url,"Hello World! This is a nice app!!!"))
        initRecyclerView(mList)
        return mList
    }

    companion object {
        const val CHOOSE_IMAGE_CODE = 16
        const val TAG = "ChatFragment"
        val UID = Firebase.auth.uid
    }
}
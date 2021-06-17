package com.ashish.messenger.ui

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.get
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
import com.ashish.messenger.Utils.getTimeId
import com.ashish.messenger.data.Chat
import com.ashish.messenger.data.Message
import com.ashish.messenger.data.MessageList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatFragment : Fragment() {
/**
 *  If navigated from Contacts fragment -> Check if chat exists and open chat using the passed Contact ID.
 *  If navigated from Chats Fragment -> Open Chat using the passsed conversation ID.
 * */
    private lateinit var recyclerView: RecyclerView
    private var contactId: String = ""
    private var conversationId: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var navController: NavController
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageSet: MutableSet<String>
    private lateinit var sendBtn : MaterialButton
    private lateinit var messageText : TextInputEditText
    private lateinit var iconSender : String
    private lateinit var iconRecipient : String
    private lateinit var binding: FragmentChatBinding

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentChatBinding>(inflater,R.layout.fragment_chat,container,false)


        navController = findNavController()
        recyclerView = binding.chatRecyclerview
        sendBtn = binding.sendButton
        messageText = binding.messageEditText





        messageList = mutableListOf()
        messageSet = mutableSetOf()

        db = Firebase.firestore
        val name = arguments?.get("contactName").toString()

        Log.i(TAG, "onCreateView: Previous Entry : ${navController.previousBackStackEntry?.destination?.id}")
        Log.i(TAG, "onCreateView: Current Entry : ${R.id.chatsFragment}")

        val previousDestination = arguments?.get("previousDestination")
        Log.i(TAG, "onCreateView: $previousDestination")

        if(previousDestination == R.id.chatsFragment.toString()) {
            conversationId = arguments?.get("conversationId").toString()
            openConversationWithConversationId()
            Log.i(TAG, "onCreateView: Navigated from Chats Fragment | Contact : $name")
        }else if(previousDestination == R.id.contactsFragment.toString()){
            Log.i(TAG, "onCreateView: Navigated from Contacts Fragment")
            contactId = arguments?.get("contactId").toString()
            openConversationWithContactId()
        }

        getIcons()

        Log.i(TAG, "onCreateView: title ${requireActivity().title}")

        binding.addMediaButton.setOnClickListener{
            var galleryIntent = Intent()
            galleryIntent.apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(galleryIntent, CHOOSE_IMAGE_CODE)
        }

        binding.sendButton.setOnClickListener{
            if(messageText.text.toString() != "") {
                if(conversationId!=null) {
                    sendMessage(messageText.text.toString())
                    messageText.setText("")
                }else {
                    startNewConversation()
                }

            }else {
                val sb = Snackbar.make(
                    requireActivity().findViewById<View>(android.R.id.content),
                    "Enter a message first.",
                    Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.design_default_color_error))
                sb.setAction("Dismiss") {
                    sb.dismiss()
                }.show()

            }
        }

        return binding.root
    }

    private fun getIcons() {
        GlobalScope.launch(Dispatchers.IO) {
            if(contactId == "") {
                val obj = db.collection("chats")
                    .document(conversationId!!).get().await()
                contactId = if(obj["participant1"].toString() == UID) {
                    obj["participant2"].toString()
                }else {
                    obj["participant1"].toString()
                }
            }

            val rec = db.collection("users")
                .document(contactId).get().await()
            iconRecipient = rec["profilePictureUrl"].toString()

            val sender = db.collection("users")
                .document(UID!!).get().await()

            iconSender = sender["profilePictureUrl"].toString()
            withContext(Dispatchers.Main) {
                Log.i(TAG, "getIcons: $iconSender | $iconRecipient")
                Log.i(TAG, "getIcons: Status | ${rec["online"].toString()}")

                initRecyclerView(messageList)
            }
        }
    }

    private fun openConversationWithContactId() {
        Log.i(TAG, "openConversationWithContactId: ID : Searching..")
        // Fetch list of messages from "message/conversationid/messages"
        GlobalScope.launch(Dispatchers.IO) {
            // Getting conversation ID first
            var conversation = db.collection("chats")
                .whereEqualTo("participant1",UID)
                .whereEqualTo("participant2",contactId)
                .get().await()
            Log.i(TAG, "openConversationWithContactId: ID : ${conversation.documents.size}")
            if(conversation.documents.size==0) {
                conversation = db.collection("chats")
                    .whereEqualTo("participant2",UID)
                    .whereEqualTo("participant1",contactId)
                    .get().await()

                Log.i(TAG, "openConversationWithContactId: Conversation was null initially")
            }
            Log.i(TAG, "openConversationWithContactId: ID : ${conversation.documents}")
            if(conversation.documents.size != 0) {
                conversationId = conversation.documents[0].id.toString()
                Log.i(TAG, "openConversationWithContactId: Found some ${conversationId}")
                openConversationWithConversationId()
            }

        }
        
    }

    private fun startNewConversation() {
        val newChat = Chat(UID!!,contactId,messageText.text.toString(),UID)
        GlobalScope.launch(Dispatchers.IO) {
            val ref = db.collection("chats")
                .add(newChat).await()
            conversationId = ref.id

            val msgList = MessageList(listOf(Message(getTimeId(),messageText.text.toString(),UID!!,
                getCurrentTime(),null,null)))
            val msgRef = db.collection("message").document(conversationId!!).set(msgList).await()
            withContext(Dispatchers.Main) {
                messageText.setText("")
                openConversationWithConversationId()
            }
        }
    }

    private fun openConversationWithConversationId() {
        val messageRef = db.collection("message")
            .document(conversationId!!)
            .addSnapshotListener{snapshot,e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val obj = snapshot?.toObject(MessageList::class.java)
                if (obj != null) {
                    for(message in obj.messages!!) {
                        if(!messageSet.contains(message.messageId)) {
                            messageList.add(message)
                            messageSet.add(message.messageId)
                            recyclerView.adapter?.notifyDataSetChanged()
                            recyclerView.layoutManager?.scrollToPosition(messageList.size-1)

                        }
                    }
                }
                Log.i(TAG, "openConversationWithContactId: ${snapshot?.id} | }")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            if(data?.clipData == null) {
                Log.i(TAG, "onActivityResult: Image Uri captured : " + data!!.data.toString())
            }
        }
    }

    private fun sendMessage(txt: String) {
        Log.i(TAG, "sendMessage: Sending...")
        // Create Message object and push
        db.collection("message")
            .document(conversationId!!)
            .update("messages",FieldValue.arrayUnion(Message(getTimeId(),txt, UID!!, getCurrentTime(),null,null)))
            .addOnSuccessListener {
                Log.i(TAG, "sendMessage: Added successfully")
            }.addOnFailureListener{
                Log.i(TAG, "sendMessage: Failed")
            }

        Log.i(TAG, "sendMessage: ${iconSender} | $iconRecipient")
    }

    private fun initRecyclerView(mList: MutableList<Message>) {

        recyclerView.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            (layoutManager as LinearLayoutManager).scrollToPosition(mList.size-1)
            adapter = ChatAdapter(mList,context.applicationContext,iconSender,iconRecipient)
        }

        recyclerView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(i4 < i8) {
                recyclerView.postDelayed({
                    recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)
                },10)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        updateRecentMessage()
    }

    private fun updateRecentMessage() {
        db.collection("chats")
            .document(conversationId!!)
            .update(mapOf(
                "recentMessage" to messageList[messageList.size-1].text,
                "author" to messageList[messageList.size-1].author
            ))
            //.update("recentMessage", messageList[messageList.size-1])
    }

    companion object {
        const val CHOOSE_IMAGE_CODE = 16
        const val TAG = "ChatFragment"
        val UID = Firebase.auth.uid

    }
}
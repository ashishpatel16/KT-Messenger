package com.ashish.messenger.ui

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.ashish.messenger.Utils.getTimeId
import com.ashish.messenger.adapter.ChatsAdapter
import com.ashish.messenger.data.*
import com.ashish.messenger.databinding.FragmentChatsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class ChatObject(val name:String, val recentMsg:String?,val profilePictureUrl:String, val conversationId: String="")
class ChatsFragment : Fragment() {

    private lateinit var mConversationList: MutableList<ChatObject>
    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser
    private val uid = user?.uid
    private lateinit var recyclerView: RecyclerView
    private lateinit var set: MutableSet<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentChatsBinding>(inflater,
            R.layout.fragment_chats,
            container,
            false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        Log.i(ChatFragment.TAG, "onCreateView: title ${requireActivity().title}")
        mConversationList = mutableListOf()
        set = mutableSetOf()


        // mConversationList.add(ChatObject("Ashish","Hi there!",""))
        recyclerView = binding.chatsRecyclerView
        initRecyclerView()
        fetchConversations()

        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_chatsFragment_to_contactsFragment)
        }

        return binding.root
    }

    private fun initRecyclerView() {
        // Initialize recycler view with existing firebase contacts
        recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            adapter = ChatsAdapter(mConversationList,context.applicationContext)
        }
        Log.i(TAG, "initRecyclerView: Init rec view done!")
    }

    private fun fetchConversations() {
        db.collection("chats")
                .whereEqualTo("participant1", uid!!)
                .addSnapshotListener{snapshot,e->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        Log.d(TAG, "Current data: ${snapshot.documents[0]["participant1"]}")
                        // If found in p1, p2 is the other recipient
                        for(chat in snapshot.documents) {
                            addChatToList(chat,"participant2")
                            Log.i(TAG, "fetchConversations: found in p1 with id: ${chat.id}")
                        }
                    } else {
                        Log.d(TAG, "Current data: null")
                    }

                }

        db.collection("chats")
                .whereEqualTo("participant2", uid)
                .addSnapshotListener{snapshot,e->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        for(chat in snapshot.documents) {
                            addChatToList(chat,"participant1")
                            Log.i(TAG, "fetchConversations: found in p2")
                        }

                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
    }

    private fun addChatToList(chat: DocumentSnapshot, id: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val id = chat[id].toString()
            val user = db.collection("users")
                    .document(id)
                    .get().await()
            Log.i(TAG, "fetchConversations: Found user: ${user["name"].toString()}")
            val name = getLocalContactName(user["phone"].toString())

            val conversation = ChatObject(name,chat["recentMessage"].toString(),
                    user["profilePictureUrl"].toString(),
                    chat.id.toString()
            )
            val conversationId = chat.id.toString()
            withContext(Dispatchers.Main) {
                if(!set.contains(conversationId)) {
                    set.add(conversationId)
                    mConversationList.add(conversation)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }

    }

    private fun getLocalContactName(phoneNum: String):String{
        val cursor = context?.contentResolver!!.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        )!!
        val PHONE_INDEX = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val NAME_INDEX = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        while(cursor.moveToNext()) {
            val name = cursor.getString(NAME_INDEX)
            val phone = cursor.getString(PHONE_INDEX).replace("\\s".toRegex(), "")
            if(phone.length >=10) {
                val processedNumber = "+91"+phone.substring(phone.length - 10, phone.length)
                if(phoneNum == processedNumber) {
                    Log.i(TAG, "getLocalContactName: $processedNumber | $phoneNum")
                    Log.i(TAG, "getLocalContactName: True")
                    return name
                }
            }
        }
        return ""
    }

    companion object{
        const val TAG = "ChatsFragment"

    }
}
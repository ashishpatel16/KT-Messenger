package com.ashish.messenger.ui

import android.Manifest
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.ashish.messenger.adapter.ContactsAdapter
import com.ashish.messenger.data.User
import com.ashish.messenger.databinding.FragmentContactsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


data class Contact(val name: String, val phone: String, var dp: String ="", var status: String ="", var contactId: String)

class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsBinding
    private lateinit var localContactsList: MutableList<Contact>
    private lateinit var firebaseContactsList: MutableList<User>
    private lateinit var firebaseContactIds : MutableList<String>
    private lateinit var cursor: Cursor
    private lateinit var phoneHashSet: MutableSet<String>
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentContactsBinding>(inflater, R.layout.fragment_contacts, container, false)
        phoneHashSet = mutableSetOf<String>()
        localContactsList = mutableListOf()
        firebaseContactIds = mutableListOf()
        firebaseContactsList = mutableListOf()

        recyclerView = binding.recyclerView
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), 1)


        cursor = context?.contentResolver!!.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        )!!

        fetchContacts()
        return binding.root
    }

    private fun initRecyclerView(mContactList: MutableList<User>, mContactIdList: MutableList<String>) {
        // Initialize recycler view with existing firebase contacts
        recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            adapter = ContactsAdapter(mContactList,mContactIdList,context)
        }
        Log.i(TAG, "initRecyclerView: Init rec view done!")
    }

    private fun fetchFirebaseUsers() {
        for(contact in localContactsList) {
            val docRef = Firebase.firestore.collection("users")
                .whereEqualTo("phone",contact.phone)
                .get()
                .addOnSuccessListener {
                    if(!it.isEmpty) {
                        val obj = it.documents[0]
                        val isActive : Boolean = obj["isOnline"] != null
                        val firebaseUser = User(contact.name,
                                contact.phone,
                                obj["profilePictureUrl"].toString(),
                                obj["status"].toString(),
                                obj["dateJoined"].toString(),
                                obj["lastSeen"].toString(),
                                isActive
                        )

                        firebaseContactIds.add(obj.id.toString())
                        firebaseContactsList.add(firebaseUser)

                        recyclerView.adapter?.notifyDataSetChanged()
                        Log.i(TAG, "test: Found User id : ${obj.id}")
                    }
                }
        }
        initRecyclerView(firebaseContactsList, firebaseContactIds)
        Log.i(TAG, "test: Finally found ${firebaseContactsList.size} Contacts in list")
    }

    private fun fetchContacts() {
        val PHONE_INDEX = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val NAME_INDEX = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        while(cursor.moveToNext()) {
            val name = cursor.getString(NAME_INDEX)
            val phone = cursor.getString(PHONE_INDEX).replace("\\s".toRegex(), "")
            if(phone.length >=10) {
                val processedNumber = "+91"+phone.substring(phone.length - 10, phone.length)
                if(!phoneHashSet.contains(processedNumber)) {
                    // Log.i(TAG, "fetchContacts: $name : $processedNumber")
                    phoneHashSet.add(processedNumber)
                    localContactsList.add(Contact(name,processedNumber,contactId = ""))
                }
            }
        }
        fetchFirebaseUsers()
    }
    
    private fun snackBar(text: String) {
        Snackbar.make(
                requireActivity().findViewById<View>(android.R.id.content),
                text,
                Snackbar.LENGTH_SHORT)
                .show()
    }

    private fun openConversation() {

    }

    companion object{
        const val TAG = "ContactsFragment"
        val UID = Firebase.auth.uid
    }

}
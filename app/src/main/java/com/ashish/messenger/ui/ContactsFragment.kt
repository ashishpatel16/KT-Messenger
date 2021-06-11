package com.ashish.messenger.ui

import android.Manifest
import android.database.Cursor
import android.os.Build
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
import com.ashish.messenger.data.User
import com.ashish.messenger.databinding.FragmentContactsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlin.math.log


data class Contact(val name: String, val phone: String, var dp: String ="")

class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsBinding
    private lateinit var localContactsList: MutableList<Contact>
    private lateinit var firebaseContactsList: MutableList<Contact>
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
        firebaseContactsList = mutableListOf()

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings

        recyclerView = binding.recyclerView
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), 1)


        cursor = context?.contentResolver!!.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        )!!
        

        fetchContacts()
        snackBar("Found ${phoneHashSet.size} contacts!")



        return binding.root
    }

    private fun initRecyclerView(mList: MutableList<Contact>) {

        recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            adapter = ContactsAdapter(mList,context)
        }
        Log.i(TAG, "initRecyclerView: Init rec view done!")
        snackBar("Found ${firebaseContactsList.size} contacts!")
    }

    private fun test() {
        val mList = mutableListOf<Contact>()
        for(contact in localContactsList) {
            val docRef = Firebase.firestore.collection("users")
                .whereEqualTo("phone",contact.phone)
                .get()
                .addOnSuccessListener {
                    if(!it.isEmpty) {
                        val obj = it.documents[0]
                        contact.dp = obj["profilePictureUrl"].toString()
                        mList.add(contact)

                        recyclerView.adapter?.notifyDataSetChanged()
                        Log.i(TAG, "test: Found ${mList.size} Contacts in list")
                    }
                }
        }
        initRecyclerView(mList)
        Log.i(TAG, "test: Finally found ${mList.size} Contacts in list")
    }

    private fun fetchFbUsers() {
        val docRef = Firebase.firestore.collection("users")
                .whereIn("phone", localContactsList.toList())
                .get()
                .addOnSuccessListener {
                    Log.i(TAG, "fetchFbUsers: ${it.size()}")
                    for(contact in it.documents) {
                        val ct = Contact(contact["name"].toString(),contact["phone"].toString())
                        Log.i(TAG, "test: Found ${firebaseContactsList.size} Contacts in list")
                        firebaseContactsList.add(ct)
                    }
                }
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
                    localContactsList.add(Contact(name,processedNumber))
                }
            }
        }

        test()
    }

//    private fun findFirebaseUsers() {
//        val docRef = Firebase.firestore.collection("users")
//                .get()
//                .addOnSuccessListener {
//                    for(user in it) {
//                        val ph = user.data["phone"].toString()
//                        if(phoneHashSet.contains(ph)){
//                            for(contact in localContactsList) {
//                                if(contact.phone == ph) {
//                                    firebaseContactsList.add(contact)
//                                    Log.i(TAG, "findFirebaseUsers: Found ${contact.phone}")
//                                }
//                            }
//                        }
//                    }
//                }.addOnFailureListener {
//                    Log.d(TAG, "findFirebaseUsers: Failed to load firebase contacts.")
//                    snackBar("Failed to Load Contacts. Please try again in sometime.")
//                }
//    }
    
    private fun snackBar(text: String) {
        Snackbar.make(
                requireActivity().findViewById<View>(android.R.id.content),
                text,
                Snackbar.LENGTH_SHORT)
                .show()
    }



    companion object{
        const val TAG = "ContactsFragment"
    }

}
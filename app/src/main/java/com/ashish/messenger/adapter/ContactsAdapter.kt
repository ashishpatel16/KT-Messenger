package com.ashish.messenger.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.ashish.messenger.data.User
import com.ashish.messenger.ui.Contact
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView

class ContactsAdapter (private val dataSet: MutableList<User>,private val UserIds:MutableList<String>, private val context: Context) :
        RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView_name: MaterialTextView = view.findViewById(R.id.contact_name)
        val textView_status: MaterialTextView = view.findViewById(R.id.contact_status)
        val img : ImageView = view.findViewById(R.id.display_picture)
        val layout: ConstraintLayout = view.findViewById(R.id.layout_contact)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_contacts, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView_name.text = dataSet[position].name
        viewHolder.textView_status.text = dataSet[position].status
        Log.i(TAG, "onBindViewHolder: ${dataSet[position].profilePictureUrl}")


        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(context)
                .load(dataSet[position].profilePictureUrl)
                .apply(requestOptions)
                .error(R.drawable.ic_contact)
                .placeholder(R.drawable.ic_contact)
                .into(viewHolder.img)

        viewHolder.layout.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("contactId",UserIds[position])

            viewHolder.itemView.findNavController()
                    .navigate(R.id.action_contactsFragment_to_chatFragment,bundle)
            Log.i("ContactsAdapter", "onBindViewHolder: passed String: ${UserIds[position]}")

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    companion object{
        const val TAG = "ContactsAdapter"
    }
}
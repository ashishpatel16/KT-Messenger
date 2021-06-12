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
import com.ashish.messenger.ui.Contact
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView

class ContactsAdapter (private val dataSet: MutableList<Contact>, context: Context) :
        RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    val mDataset = dataSet
    val mContext = context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView_name: MaterialTextView = view.findViewById(R.id.contact_name)
        val textView_status: MaterialTextView = view.findViewById(R.id.contact_status)
        val img : ImageView = view.findViewById(R.id.display_picture)
        val layout: ConstraintLayout = view.findViewById(R.id.layout_contact)

        init {
            // Define click listener for the ViewHolder's View.


        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_contacts, viewGroup, false)
        Toast.makeText(view.context,
                "Found some ${mDataset.size} contacts",
                Toast.LENGTH_SHORT)
                .show()

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView_name.text = dataSet[position].name
        viewHolder.textView_status.text = dataSet[position].status
        Log.i("DP STATUS", "onBindViewHolder: ${dataSet[position].dp}")


        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(mContext)
                .load(dataSet[position].dp)
                .apply(requestOptions)
                .error(R.drawable.ic_contact)
                .placeholder(R.drawable.ic_contact)
                .into(viewHolder.img)

        viewHolder.layout.setOnClickListener{
            Toast.makeText(mContext,
                "${viewHolder.textView_name.text.toString()}",
                Toast.LENGTH_SHORT)
                .show()
            var bundle = Bundle()
            bundle.putString("contactId",dataSet[position].contactId)

            viewHolder.itemView.findNavController()
                    .navigate(R.id.action_contactsFragment_to_chatFragment,bundle)
            Log.i("ContactsAdapter", "onBindViewHolder: passed String: ${dataSet[position].contactId}")

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
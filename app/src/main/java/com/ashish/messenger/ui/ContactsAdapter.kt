package com.ashish.messenger.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView

class ContactsAdapter (private val dataSet: MutableList<Contact>, context: Context) :
        RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    val mDataset = dataSet
    val mContext = context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView_name: MaterialTextView
        val textView_phone: MaterialTextView
        val img : ImageView
        val layout: ConstraintLayout

        init {
            // Define click listener for the ViewHolder's View.
            textView_name = view.findViewById(R.id.contact_name)
            textView_phone = view.findViewById(R.id.contact_phone)
            img = view.findViewById(R.id.display_picture)
            layout = view.findViewById(R.id.layout_contact)
            layout.setOnClickListener{
                Toast.makeText(view.context,
                        "${textView_name.text.toString()}",
                        Toast.LENGTH_SHORT)
                        .show()
            }
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
        viewHolder.textView_phone.text = dataSet[position].phone
        Log.i("DP STATUS", "onBindViewHolder: ${dataSet[position].dp}")


        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(mContext)
                .load(dataSet[position].dp)
                .apply(requestOptions)
                .error(R.drawable.ic_contact)
                .placeholder(R.drawable.ic_contact)
                .into(viewHolder.img)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
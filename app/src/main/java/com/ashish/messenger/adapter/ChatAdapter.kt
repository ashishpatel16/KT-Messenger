package com.ashish.messenger.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.ashish.messenger.Utils.getUId
import com.ashish.messenger.data.Message
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class ChatAdapter (private val dataSet: MutableList<Message>, context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val mDataset = dataSet
    val mContext = context

    class ViewHolderSender(view: View) : RecyclerView.ViewHolder(view) {
        val senderIcon: ShapeableImageView
        val senderText: TextView

        init {
            // Define click listener for the ViewHolder's View.
            senderIcon = view.findViewById(R.id.chat_sender_picture)
            senderText = view.findViewById(R.id.chat_sender_text)
        }
    }

    class ViewHolderReceiver(view: View) : RecyclerView.ViewHolder(view) {
        val receiverIcon: ShapeableImageView
        val receiverText: TextView

        init {
            // Define click listener for the ViewHolder's View.
            receiverIcon = view.findViewById(R.id.chat_receiver_picture)
            receiverText = view.findViewById(R.id.chat_receiver_text)
        }
    }




    override fun getItemViewType(position: Int): Int {
        return if(dataSet[position].author == getUId()) 0
        else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_row_sender, parent, false)
            ViewHolderSender(view)
        }else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_row_receiver, parent, false)
            ViewHolderReceiver(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(dataSet[position].author == getUId()) {
            val vh = holder as ViewHolderSender
            holder.senderText.text = dataSet[position].text
//            Glide.with(mContext)
//                .load(dataSet[position].)
//                .error(R.drawable.ic_contact)
//                .placeholder(R.drawable.ic_contact)
//                .into(holder.senderIcon)
        }else {
            val vh = holder as ViewHolderReceiver
            holder.receiverText.text = dataSet[position].text
//            Glide.with(mContext)
//                .load(dataSet[position].id)
//                .error(R.drawable.ic_contact)
//                .placeholder(R.drawable.ic_contact)
//                .into(holder.receiverIcon)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


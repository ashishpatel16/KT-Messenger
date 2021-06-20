package com.ashish.messenger.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.Typeface.BOLD_ITALIC
import android.graphics.Typeface.DEFAULT_BOLD
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.get
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.ashish.messenger.Utils.getUId
import com.ashish.messenger.data.Message
import com.ashish.messenger.ui.ChatFragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class ChatAdapter (private val dataSet: MutableList<Message>, private val context: Context,private val senderIcon:String,private val recipientIcon:String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mediaText: String = "Sent you an attachment."

    class ViewHolderSender(view: View) : RecyclerView.ViewHolder(view) {
        val senderIcon: ShapeableImageView
        val senderText: TextView
        val btnSenderAttachment: TextView

        init {
            // Define click listener for the ViewHolder's View.
            senderIcon = view.findViewById(R.id.chat_sender_picture)
            senderText = view.findViewById(R.id.chat_sender_text)
            btnSenderAttachment = view.findViewById(R.id.btn_attachment_sender)
        }
    }

    class ViewHolderReceiver(view: View) : RecyclerView.ViewHolder(view) {
        val receiverIcon: ShapeableImageView
        val receiverText: TextView
        val btnReceiverAttachment: TextView

        init {
            // Define click listener for the ViewHolder's View.
            receiverIcon = view.findViewById(R.id.chat_receiver_picture)
            receiverText = view.findViewById(R.id.chat_receiver_text)
            btnReceiverAttachment = view.findViewById(R.id.btn_attachment_receiver)
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
            Glide.with(context)
                .load(senderIcon)
                .error(R.drawable.ic_contact)
                .placeholder(R.drawable.ic_contact)
                .into(holder.senderIcon)
            if(dataSet[position].mediaUrl != null && dataSet[position].mediaUrl.toString() != "") {
                Log.i("TAG", "onBindViewHolder: found media : ${dataSet[position].mediaUrl}")
                holder.senderText.text = mediaText
                holder.senderText.typeface = DEFAULT_BOLD
                holder.btnSenderAttachment.visibility = View.VISIBLE



                holder.btnSenderAttachment.setOnClickListener{
                    val bundle = Bundle()
                    bundle.putString("MEDIA_URL",dataSet[position].mediaUrl)
                    holder.itemView.findNavController().navigate(R.id.action_chatFragment_to_imageDialog,bundle)
                }
            }
        }else {
            val vh = holder as ViewHolderReceiver
            holder.receiverText.text = dataSet[position].text
            Glide.with(context)
                .load(recipientIcon)
                .error(R.drawable.ic_contact)
                .placeholder(R.drawable.ic_contact)
                .into(holder.receiverIcon)
            if(dataSet[position].mediaUrl != null && dataSet[position].mediaUrl.toString() != "") {
                Log.i("TAG", "onBindViewHolder: found media")
                holder.receiverText.text = mediaText
                holder.btnReceiverAttachment.visibility = View.VISIBLE
                holder.btnReceiverAttachment.setOnClickListener{

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


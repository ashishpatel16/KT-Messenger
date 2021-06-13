package com.ashish.messenger.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.ashish.messenger.data.Conversation
import com.ashish.messenger.ui.ChatObject
import com.bumptech.glide.Glide

class ChatsAdapter (private val dataSet: MutableList<ChatObject>, private val context: Context) :
        RecyclerView.Adapter<ChatsAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val displayPicture: ImageView = view.findViewById(R.id.chats_profile_picture)
        val chatName: TextView = view.findViewById(R.id.tv_chat_name)
        val recentMessage : TextView = view.findViewById(R.id.tv_recent_message)
        val layout: ConstraintLayout = view.findViewById(R.id.layout_chats)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_chats, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chatName.text = dataSet[position].name.toString()
        holder.recentMessage.text = dataSet[position].recentMsg.toString()
        Glide.with(context)
                .load(dataSet[position].profilePictureUrl.toString())
                .into(holder.displayPicture)

        holder.layout.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("conversationId",dataSet[position].conversationId)
            holder.itemView.findNavController().navigate(R.id.action_chatsFragment_to_chatFragment,bundle)
        }

    }

    override fun getItemCount(): Int {
        Log.i(TAG, "getItemCount: Size : ${dataSet.size}")
        return dataSet.size

    }

    companion object{
        const val TAG = "ChatsAdapter"
    }
}
package com.ashish.messenger.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ashish.messenger.R
import com.bumptech.glide.Glide

class MediaAdapter(private val dataSet:MutableList<String>, private val context: Context): RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaImage: ImageView = view.findViewById(R.id.media_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.media_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
                .load(dataSet[position])
                .into(holder.mediaImage)

    }

    override fun getItemCount(): Int {
        Log.i(TAG, "getItemCount: Size : ${dataSet.size}")
        return dataSet.size

    }

    companion object{
        const val TAG = "MediaAdapter"
    }
}
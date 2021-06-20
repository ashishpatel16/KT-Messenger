package com.ashish.messenger.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.ashish.messenger.R
import com.ashish.messenger.Utils.getTimeId
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class ImageDialog() : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val url = arguments?.get("MEDIA_URL").toString()
            Log.i("Dialog",url)
            val view = inflater.inflate(R.layout.dialog_image,null)

            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            view.findViewById<ImageView>(R.id.dialog_imageview).setImageBitmap(resource)
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })

            builder.setView(view)
                    .setNegativeButton("Close",
                            DialogInterface.OnClickListener { dialog, id ->
                                getDialog()!!.cancel()
                            })
                    .setPositiveButton("Save",
                            DialogInterface.OnClickListener { dialog, id ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    Glide.with(requireContext())
                                            .asBitmap()
                                            .load(url) // sample image
                                            .into(object: CustomTarget<Bitmap>() {
                                                override fun onLoadCleared(placeholder: Drawable?) {

                                                }

                                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                                    Log.i("Dialog","Ready")
                                                    downloadImage(resource)
                                                }
                                            })


                                }
                            })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun downloadImage(bmap: Bitmap) {
        val imageFileName = "JPEG_" + getTimeId() + ".jpg"
        val savedImageURL: String = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                bmap,
                imageFileName,
                imageFileName
        )

    }
}
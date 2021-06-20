package com.ashish.messenger.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ashish.messenger.R
import com.ashish.messenger.Utils.getCurrentTime
import com.ashish.messenger.data.User
import com.ashish.messenger.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var storage: FirebaseStorage
    private var uploadedPictureUrl: String = ""
    private var localImageUri: Uri? = null
    private lateinit var mUserId: String
    private lateinit var mUserStatus: String
    private lateinit var mUser: FirebaseUser


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        storage = FirebaseStorage.getInstance()
        mUser = Firebase.auth.currentUser!!
        mUserId = Firebase.auth.currentUser?.uid.toString()
        reloadData()

        binding.btnSaveProfile.setOnClickListener{
            val phone = Firebase.auth.currentUser?.phoneNumber.toString()

            Log.i(TAG, "onCreateView: Phone, UID : $phone , ${mUserId}")
            if( binding.etName.text.toString().equals("")) {
                activity?.findViewById<View>(android.R.id.content)?.let { it1 ->
                    Snackbar.make(
                            it1,
                            "You need to enter your display name.",
                            Snackbar.LENGTH_LONG)
                            .setBackgroundTint(resources.getColor(R.color.design_default_color_error))
                            .show()
                }
            }else {
                saveProfile(phone, binding.etStatus.text.toString(), binding.etName.text.toString())
                findNavController().navigate(R.id.action_profileFragment_to_chatsFragment)
            }
        }

        binding.shapeableImageView.setOnClickListener {
            val openGalleryIntent = Intent()
            openGalleryIntent.type = "image/*"
            openGalleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(openGalleryIntent, "Select Picture"),
                PICK_IMAGE)
            binding.btnSaveProfile.isEnabled = false
        }

        (activity as MainActivity).setUpUI()

        return  binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            localImageUri = data!!.data
            uploadProfilePicture(localImageUri!!)
        }
    }

    private fun saveProfile(phone: String, status: String, name: String) {
        val user = User(name,phone,uploadedPictureUrl,status, getCurrentTime(),"",true)
        val db = Firebase.firestore
        db.collection("users")
                .document(mUserId)
            .set(user, SetOptions.merge()) // Update details if user already exists
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${mUserId}")
                activity?.let { it1 ->
                    Snackbar.make(
                            it1.findViewById<View>(android.R.id.content),
                            "You're all set up.",
                            Snackbar.LENGTH_LONG)
                            .setBackgroundTint(resources.getColor(R.color.teal_200))
                            .show()
                }
            }
    }

    private fun uploadProfilePicture(uri: Uri) {
        val path = "profile_pictures/${UUID.randomUUID()}.png"
        val dpRef = storage.reference.child(path)
        val bmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

        Log.i(TAG, "uploadProfilePicture: Image Found I guess")
        binding.shapeableImageView.setImageBitmap(bmap)

        val uploadTask = dpRef.putFile(uri)
                .addOnSuccessListener {
                activity?.let { it1 ->
                    Snackbar.make(
                            it1.findViewById<View>(android.R.id.content),
                            "Profile Picture Uploaded",
                            Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(resources.getColor(R.color.teal_200))
                            .show()
                }
                dpRef.downloadUrl.addOnCompleteListener{
                    val downloadUri = it.result.toString()
                    uploadedPictureUrl = downloadUri
                    binding.btnSaveProfile.isEnabled = true
                    Log.i(TAG, "uploadProfilePicture: $uploadedPictureUrl")
            }
        }.addOnFailureListener{
            Snackbar.make(
                requireActivity().findViewById<View>(android.R.id.content),
                "Upload Failed.",
                Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.design_default_color_error))
                .show()
        }
    }

    private fun reloadData() {
        val userRef = Firebase.firestore
                .collection("users")
                .document(mUserId)
                .get()
                .addOnSuccessListener {
                    if(it.exists()){
                        Log.i(TAG, "reloadData: found Url : ${it["profilePictureUrl"]}")
                        with(this).load(it["profilePictureUrl"]).into(binding.shapeableImageView)
                        binding.etName.setText(it["name"].toString())
                        binding.etStatus.setText(it["status"].toString())
                        uploadedPictureUrl = it["profilePictureUrl"].toString()
                        mUserStatus = it["status"].toString()
                    }
                }
    }

    companion object{
        const val TAG = "profileFragment"
        const val PICK_IMAGE = 200
    }

}
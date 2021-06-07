package com.ashish.messenger

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.ashish.messenger.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var storedVerificationId: String? = ""
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this,navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

//    override fun onBackPressed() {
//        val navDestionation = navController.currentDestination
//        if(navDestionation!=null && navDestionation.id == R.id.splashScreenFragment) {
//             finish()
//            return
//        }
//        super.onBackPressed()
//    }
    // Initialize Firebase Auth
//        auth = Firebase.auth
//
//
//        var phone = "+918319520553"
//
//        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                Log.d(TAG, "onVerificationCompleted:$credential")
//                signInWithPhoneAuthCredential(credential)
//            }
//
//            override fun onVerificationFailed(e: FirebaseException) {
//                Log.w(TAG, "onVerificationFailed", e)
//
//                if (e is FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                } else if (e is FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                }
//
//                Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT)
//            }
//
//            override fun onCodeSent(
//                verificationId: String,
//                token: PhoneAuthProvider.ForceResendingToken
//            ) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:$verificationId")
//
//                // Save verification ID and resending token so we can use them later
//                val storedVerificationId = verificationId
//                val resendToken = token
//
//            }
//        }
//
//        // createAccount(phone)
//
////            binding.buttonSubmit.setOnClickListener {
////                val code: String = binding.editTextCode.text.toString()
////                Log.i(TAG, "verification-token:" + storedVerificationId + " code-found:" + code)
////                verifyPhoneNumberWithCode(storedVerificationId!!, code)
////            }
//
//        }
//
//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if(currentUser != null){
//            Toast.makeText(this, "User already Logged in",Toast.LENGTH_SHORT)
//        }else {
//            Toast.makeText(this, "Login Required",Toast.LENGTH_SHORT)
//        }
//    }
//
//
//    private fun createAccount(phoneNumber:String) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//                .setPhoneNumber(phoneNumber)       // Phone number to verify
//                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                .setActivity(this)                 // Activity (for callback binding)
//                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
//                .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
//
//    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
//        // [START verify_with_code]
//        val credential = PhoneAuthProvider.getCredential(verificationId, code)
//        // [END verify_with_code]
//    }
//
//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "signInWithCredential:success")
//
//                        val user = task.result?.user
//                        Toast.makeText(this, "Hooray",Toast.LENGTH_SHORT)
//                    } else {
//                        // Sign in failed, display a message and update the UI
//                        Log.w(TAG, "signInWithCredential:failure", task.exception)
//                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                            // The verification code entered was invalid
//                        }
//                        // Update UI
//                    }
//                }
//    }
//
//    companion object {
//        private const val TAG = "PhoneAuthActivity"


}
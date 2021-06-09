package com.ashish.messenger.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ashish.messenger.R
import com.ashish.messenger.databinding.ActivityMainBinding
import com.ashish.messenger.databinding.FragmentLoginBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    lateinit var navController: NavController
    private var storedVerificationId: String? = ""
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var contextView: View
    private lateinit var btn: MaterialButton
    private lateinit var codeInputText: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        // Primary Initializations
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false)
        btn = binding.btnSubmit
        codeInputText = binding.editTextCode
        auth = Firebase.auth
        contextView = requireActivity().findViewById<View>(android.R.id.content)
        navController = findNavController()
        navController.graph.startDestination = R.id.chatsFragment


        (requireActivity() as AppCompatActivity).supportActionBar?.hide() // Keeping the action bar hidden for login screen
        binding.btnSubmit.setOnClickListener{
            val phone = binding.editTextPhone.text.toString()
            if(phone.length == 10) {
                Snackbar.make(contextView,"Invalid Phone Number. Please retry.",Snackbar.LENGTH_SHORT).show()
            }
            codeInputText.visibility = View.VISIBLE
            createAccount("+91$phone")
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Snackbar.make(contextView,"Welcome Back, Captain!",Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.purple_200))
                .setTextColor(resources.getColor(R.color.purple_700))
                .show()
            navigateToNextFragment(R.id.action_loginFragment_to_chatsFragment)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                Snackbar.make(contextView,"Something went wrong!",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(R.color.design_default_color_error))
                    .show()

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                val storedVerificationId = verificationId
                val resendToken = token

            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Snackbar.make(contextView,"Request Timed out. Try again.",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(R.color.design_default_color_error))
                    .show()
            }
        }
    }

    private fun createAccount(phoneNumber:String) {
        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        // [END verify_with_code]
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = task.result?.user
                        Snackbar.make(contextView,"Hooray! Login Successful.",Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(resources.getColor(R.color.teal_200))
                                .show()
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        var errorMessage : String = "Something went wrong! Please retry."
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Invalid Code. Make sure you entered the right code."
                        }
                        Snackbar.make(contextView,errorMessage,Snackbar.LENGTH_LONG)
                            .setBackgroundTint(resources.getColor(R.color.design_default_color_error))
                            .show()
                    }
                }
    }

    private fun navigateToNextFragment(destination: Int) {
        navController.navigate(destination)
    }

    companion object{
        const val TAG = "LoginFragment"
    }
}
package com.o.covid19volunteerapp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.o.covid19volunteerapp.databinding.ActivityPhoneVerificationBinding
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_phone_verification.*


class PhoneVerificationActivity : AppCompatActivity() {

    lateinit var user : User
    private var password : String? = null

    private lateinit var viewmodel : FirebaseViewmodel

    private lateinit var binding : ActivityPhoneVerificationBinding

    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private val TAG = "PhoneVerification"

    private var storedVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.o.covid19volunteerapp.R.layout.activity_phone_verification)

        setSupportActionBar(toolbar)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        initializeCallbacks()

        getExtrasFromParent()

        fab.setOnClickListener { view ->
            checkCode()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")

                binding.content.codeEditText.setText(credential.smsCode)

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
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
                storedVerificationId = verificationId

                // ...
            }
        }
    }

    private fun getExtrasFromParent() {
        val gson = Gson()
        val userString : String? = intent.getStringExtra("user")
        user = gson.fromJson(userString, User::class.java)
        password = intent.getStringExtra("password")

        sendVerificationCode(user.phone)
    }

    private fun sendVerificationCode(phone: String) {
        viewmodel.sendVerificationCode(phone, this, callbacks)
    }

    private fun checkCode() {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, binding.content.codeEditText.text.toString())
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        //TODO: move to repository

        showProgress()
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Successful mean that the phone is verified
                    val newUser = it.result?.user
                    linkEmailAndPhoneAuth(user.email, password!!)
                } else {
                    Snackbar.make(binding.layout,
                        "Something went wrong. Please try again.", Snackbar.LENGTH_LONG).show()
                    hideProgress()
                }
            }
    }
    private fun linkEmailAndPhoneAuth(email : String, password : String) {
        //TODO: move to repository
        val credential = EmailAuthProvider.getCredential(email, password)

        val auth = FirebaseAuth.getInstance()
        auth.getCurrentUser()!!.linkWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "linkWithCredential:success")
                        val user = task.result!!.user

                        Toast.makeText(
                            this,
                            "Success.",
                            Toast.LENGTH_LONG).show()

                        uploadUserData(this.user, user!!.uid)
                    } else {
                        Snackbar.make(binding.layout,
                            "Something went wrong. Please try again.", Snackbar.LENGTH_LONG).show()
                        hideProgress()
                    }
                })
    }

    private fun uploadUserData(user: User, uid : String) {
        viewmodel.addUser(user, uid)

        //TODO: go to next activity
    }

    fun showProgress() {
        binding.content.codeEditText.isClickable = false
        binding.fab.visibility = View.INVISIBLE
        binding.content.progress.visibility = View.VISIBLE
    }

    fun hideProgress() {
        binding.content.codeEditText.isClickable = true
        binding.fab.visibility = View.VISIBLE
        binding.content.progress.visibility = View.INVISIBLE
    }

}

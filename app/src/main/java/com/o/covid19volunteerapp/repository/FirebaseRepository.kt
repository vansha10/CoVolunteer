package com.o.covid19volunteerapp.repository

import android.app.Activity
import android.app.Application
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class FirebaseRepository {


    companion object {
        var instance: FirebaseRepository? = null
        fun getFirebaseInstance(): FirebaseRepository {
            if (instance == null) {
                instance = FirebaseRepository()
            }
            return instance!!
        }
    }

    init {

    }

    fun sendVerificationCode(phoneNumber : String, activity : Activity,
                             callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            activity, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
    }

}
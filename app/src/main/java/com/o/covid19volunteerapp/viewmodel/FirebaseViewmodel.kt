package com.o.covid19volunteerapp.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthProvider
import com.o.covid19volunteerapp.repository.FirebaseRepository

class FirebaseViewmodel : ViewModel() {
    private var firebaseRepository: FirebaseRepository? = null

    fun init() {
        firebaseRepository = FirebaseRepository.getFirebaseInstance()
    }

    fun sendVerificationCode(phoneNumber : String, activity : Activity,
                             callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        firebaseRepository!!.sendVerificationCode(phoneNumber, activity, callbacks)
    }
}
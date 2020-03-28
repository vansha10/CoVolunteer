package com.o.covid19volunteerapp.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import com.o.covid19volunteerapp.model.Request
import com.o.covid19volunteerapp.model.User
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

    fun addUser(user : User, uid : String) {
        firebaseRepository!!.addUser(user, uid)
    }

    fun loginUser(email : String, password : String) : MutableLiveData<FirebaseUser> {
        return firebaseRepository!!.loginUser(email, password)
    }

    fun getUserData(uid : String) : MutableLiveData<User>{
        return firebaseRepository!!.getUserData(uid)
    }

    fun addRequest(request: Request) {
        firebaseRepository!!.addRequest(request)
    }
}
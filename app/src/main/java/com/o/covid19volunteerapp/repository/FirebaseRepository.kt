package com.o.covid19volunteerapp.repository

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.o.covid19volunteerapp.activity.LoginActivity
import com.o.covid19volunteerapp.model.User
import java.util.concurrent.TimeUnit

class FirebaseRepository {


    private val TAG: String = "Firebase"
    private var db : FirebaseFirestore = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()

    companion object {
        var instance: FirebaseRepository? = null
        fun getFirebaseInstance(): FirebaseRepository {
            if (instance == null) {
                instance = FirebaseRepository()
            }
            return instance!!
        }
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

    fun loginUser(email : String, password : String) : MutableLiveData<FirebaseUser> {
        val firebaseUser : MutableLiveData<FirebaseUser> = MutableLiveData()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    firebaseUser.value = user

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    firebaseUser.value = null
                    // ...
                }

                // ...
            }
        return firebaseUser
    }

    fun addUser(user : User, uid : String) {
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {Log.d(TAG, "user added") }
            .addOnFailureListener { exception ->   Log.w(TAG, "Error adding document", exception)}
    }

}
package com.o.covid19volunteerapp.repository

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.o.covid19volunteerapp.model.*
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

    fun getUserData(uid : String) : MutableLiveData<User> {
        val userData = MutableLiveData<User>()

        val docRef = db.collection("users").document(uid)



        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val user = documentSnapshot.toObject<User>()
                userData.value = user
            } else {
                Log.d(TAG, "User does not exist")
                userData.value = null
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
            userData.value = null
        }

        return userData
    }

    fun listenUserDataChange(uid: String) : MutableLiveData<User> {
        val userData = MutableLiveData<User>()

        val docRef = db.collection("users").document(uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d(TAG, "get failed with ", e)
                userData.value = null
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject<User>()
                userData.value = user
                Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                Log.d(TAG, "User does not exist")
                userData.value = null
            }
        }
        return userData
    }

    fun addRequest(request : Request, uid : String) : MutableLiveData<Boolean> {
        var isSuccessful = MutableLiveData<Boolean>()

        db.collection("requests")
            .add(request)
            .addOnSuccessListener {
                isSuccessful.value = true
                db.collection("requests")
                    .document(it.id)
                    .update("id", it.id)
                addUserRequest(request, uid, it.id)
                Log.d(TAG, "request added")
                }
            .addOnFailureListener {
                Log.d(TAG, "error adding request", it)
                isSuccessful.value = false
            }
        return isSuccessful
    }

    fun addUserRequest(request: Request, uid : String, requestId : String) {
        val userRequest = UserRequest()
        userRequest.setRequest(request)
        userRequest.requestId = requestId
        db.collection("users")
            .document(uid)
            .update("requests", FieldValue.arrayUnion(userRequest))
            .addOnSuccessListener { }
            .addOnFailureListener { exception ->
                Log.d(TAG, "error adding request", exception)
            }
    }

    fun getRequestsByLocation(locality: Locality) : MutableLiveData<List<Request>> {
        val requests = MutableLiveData<List<Request>>()

        val docRef = db.collection("requests")

        val query = docRef.whereEqualTo("locality", locality)

        query.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            } else {
                val reqList = MutableList(0) { Request() }
                for (qs in querySnapshot!!) {
                    val req = qs.toObject<Request>()
                    reqList.add(req)
                }
                requests.value = reqList
            }
        }

        return requests
    }

    fun addResponse(response: Response, uid: String, request: Request):  MutableLiveData<Boolean> {
        val isSuccessful = MutableLiveData<Boolean>()
        val userRequest = UserRequest()
        userRequest.setRequest(request)
        userRequest.responses.add(response)

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val user = documentSnapshot.toObject<User>()
                    val newRequests = MutableList(0) { UserRequest() }
                    for (req in user!!.requests) {
                        if (req.requestId == request.id) {
                            req.responses.add(response)
                        }
                        newRequests.add(req)
                    }
                    db.collection("users")
                        .document(uid)
                        .update("requests", newRequests)
                        .addOnSuccessListener {
                            isSuccessful.value = true
                            Log.d(TAG, "response added")
                        }
                        .addOnFailureListener {
                            isSuccessful.value = false
                            Log.d(TAG, "error adding response", it)
                        }
                }
            }
            .addOnFailureListener { isSuccessful.value = false }
        return isSuccessful
    }
}
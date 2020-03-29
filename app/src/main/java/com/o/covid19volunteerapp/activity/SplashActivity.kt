package com.o.covid19volunteerapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (userLoggedIn()) {
            getUserData()
        } else {
            startWelcomeActivity()
        }
    }

    private fun getUserData() {
        val viewmodel : FirebaseViewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()
        val userDataObserver = Observer<User> { user ->
            if (user != null) {
                startMainActivity(user)
            } else {
                startWelcomeActivity()
            }
        }
        viewmodel.getUserData(FirebaseAuth.getInstance().currentUser!!.uid).observe(this, userDataObserver)
    }

    private fun startMainActivity(user: User?) {
        val intent = Intent(this, MainActivity::class.java)
        val gson = Gson()
        intent.putExtra("user", gson.toJson(user))
        startActivity(intent)
    }

    private fun startWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }

    private fun userLoggedIn() : Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}

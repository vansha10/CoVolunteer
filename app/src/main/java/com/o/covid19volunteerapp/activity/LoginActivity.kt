package com.o.covid19volunteerapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseUser
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.databinding.ActivityLoginBinding
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    private lateinit var viewmodel : FirebaseViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setSupportActionBar(toolbar)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        binding.content.loginButton.setOnClickListener{login()}

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun login() {
        val email = binding.content.email.text.toString()
        val password = binding.content.password.text.toString()

        val loginUserObserver = Observer<FirebaseUser> { user ->
            if (user != null) {
                Toast.makeText(this, user.phoneNumber, Toast.LENGTH_SHORT).show()
            }
        }

        viewmodel.loginUser(email, password).observe(this, loginUserObserver)
    }

}

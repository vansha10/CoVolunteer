package com.o.covid19volunteerapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.databinding.ActivityLoginBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initialiseButtons()
    }

    private fun initialiseButtons() {
        binding.loginButton.setOnClickListener {  }
        binding.signupButton.setOnClickListener {  }
    }
}

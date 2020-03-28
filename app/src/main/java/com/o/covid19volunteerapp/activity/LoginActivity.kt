package com.o.covid19volunteerapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.o.covid19volunteerapp.databinding.ActivityLoginBinding
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    private lateinit var viewmodel: FirebaseViewmodel

    private val TAG = "Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.o.covid19volunteerapp.R.layout.activity_login)
        setSupportActionBar(toolbar)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        binding.content.loginButton.setOnClickListener { validateInput() }

        binding.content.forgotPassword.setOnClickListener { forgotPassword() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun validateInput() {
        var focusView: View? = null
        if (binding.content.email.text.isEmpty()) {
            focusView = binding.content.email
            binding.content.email.error = "Please enter your email"
        }
        if (binding.content.password.text.isEmpty()) {
            if (focusView == null)
                focusView = binding.content.password
            binding.content.password.error = "Please enter your password"
        }
        if (focusView == null) {
            login()
        } else {
            focusView.requestFocus()
        }
    }

    private fun login() {

        hideKeyboard()

        showProgress()

        val email = binding.content.email.text.toString()
        val password = binding.content.password.text.toString()

        val loginUserObserver = Observer<FirebaseUser> { user ->
            if (user != null) {
                Toast.makeText(this, "Logging in", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("uid", user.uid)
                startActivity(intent)
            } else {
                hideProgress()
                Snackbar.make(
                    binding.layout,
                    "Please check your email and password", Snackbar.LENGTH_LONG
                ).show()
            }
        }

        viewmodel.loginUser(email, password).observe(this, loginUserObserver)
    }

    private fun forgotPassword() {
        hideKeyboard()

        if (binding.content.email.text.isEmpty()) {
            binding.content.email.requestFocus()
            binding.content.email.error = "Please enter your email"
        } else {
            showProgress()
            FirebaseAuth.getInstance().sendPasswordResetEmail(binding.content.email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                        hideProgress()
                        Snackbar.make(binding.layout,
                            "Password reset email has been sent!", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

    }

    private fun hideKeyboard() {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {

        }

    }

    fun showProgress() {
        binding.content.loginButton.isClickable = false
        binding.content.email.isClickable = false
        binding.content.password.isClickable = false
        binding.content.progress.visibility = View.VISIBLE
    }

    fun hideProgress() {
        binding.content.loginButton.isClickable = true
        binding.content.email.isClickable = true
        binding.content.password.isClickable = true
        binding.content.progress.visibility = View.INVISIBLE
    }

}

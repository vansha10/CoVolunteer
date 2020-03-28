package com.o.covid19volunteerapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.databinding.ActivityAddRequestBinding
import com.o.covid19volunteerapp.model.Request
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_add_request.*
import kotlinx.android.synthetic.main.content_add_request.*

class AddRequestActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddRequestBinding
    lateinit var viewmodel : FirebaseViewmodel
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_request)
        setSupportActionBar(toolbar)

        val gson = Gson()
        user = gson.fromJson(intent.getStringExtra("user"), User::class.java)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        fab.setOnClickListener { sendRequest() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun sendRequest() {
       val requestText = binding.content.request.text.toString()

        if (requestText.isEmpty()) {
            Snackbar.make(binding.layout,
                "Request cannot be empty",
                Snackbar.LENGTH_LONG).show()
        } else {
            val request = Request(requestText, user.phone, user.name)
            viewmodel.addRequest(request)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

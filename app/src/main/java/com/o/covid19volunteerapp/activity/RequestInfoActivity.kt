package com.o.covid19volunteerapp.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat.getExtras
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.databinding.ActivityRequestInfoBinding
import com.o.covid19volunteerapp.model.Request
import com.o.covid19volunteerapp.model.Response
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel

import kotlinx.android.synthetic.main.activity_request_info.*

class RequestInfoActivity : AppCompatActivity() {

    lateinit var request : Request
    lateinit var user : User
    lateinit var binding : ActivityRequestInfoBinding
    lateinit var viewmodel: FirebaseViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_info)
        setSupportActionBar(toolbar)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        getExtras()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getExtras() {
        val gson = Gson()
        request = gson.fromJson(intent.getStringExtra("request"), Request::class.java)
        user = gson.fromJson(intent.getStringExtra("user"), User::class.java)
        updateUI()
    }

    private fun updateUI() {
        binding.content.name.text = request.name
        binding.content.locality.text = "${request.locality.postalCode}, ${request.locality.country}"
        binding.content.text.text = request.requestText
        binding.content.volunteerButton.setOnClickListener {
            sendVolunteerRequest()
        }
    }

    private fun sendVolunteerRequest() {
        showProgress()
        val response = Response(user.name, user.phone, FirebaseAuth.getInstance().currentUser!!.uid)
        val responseObserver = Observer<Boolean> { isSuccessful ->
            if (isSuccessful != null) {
                if (isSuccessful) {
                    Toast.makeText(this, "Thank you!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    hideProgress()
                    Snackbar.make(binding.layout,
                        "Something went wrong. Please try again.",
                        Snackbar.LENGTH_LONG).show()
                }
            }
        }
        viewmodel.addResponse(response, request.uid, request).observe(this, responseObserver)
    }

    override fun onBackPressed() {
        finish()
    }

    fun showProgress() {
        binding.content.volunteerButton.isClickable = false
        binding.content.progress.visibility = View.VISIBLE
    }
    fun hideProgress() {
        binding.content.volunteerButton.isClickable = true
        binding.content.progress.visibility = View.GONE
    }
}

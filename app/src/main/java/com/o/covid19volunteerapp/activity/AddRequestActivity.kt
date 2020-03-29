package com.o.covid19volunteerapp.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.o.covid19volunteerapp.BuildConfig
import com.o.covid19volunteerapp.databinding.ActivityAddRequestBinding
import com.o.covid19volunteerapp.model.Request
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_add_request.*
import java.util.*


class AddRequestActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddRequestBinding
    lateinit var viewmodel : FirebaseViewmodel
    lateinit var user : User
    val apiKey : String = BuildConfig.GOOGLE_MAP_KEY
    val TAG = "AddRequest"
    var locality : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.o.covid19volunteerapp.R.layout.activity_add_request)
        setSupportActionBar(toolbar)

        val gson = Gson()
        user = gson.fromJson(intent.getStringExtra("user"), User::class.java)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        initPlacesApi()

        fab.setOnClickListener { sendRequest() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPlacesApi() {
        // Initialize the SDK
        Places.initialize(applicationContext, apiKey)

        // Create a new Places client instance
        val placesClient = Places.createClient(this)

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(com.o.covid19volunteerapp.R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment?

        // Specify the types of place data to return.
        autocompleteFragment!!.setPlaceFields(listOf(Place.Field.ADDRESS, Place.Field.ID))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                binding.content.selectedLocality.text = "You have selected: ${place.address}"
                locality = place.address
                Log.i(TAG, "Place: " + place.address)
            }
            override fun onError(status: Status) {
                Snackbar.make(binding.layout,
                    "Something went wrong. Please try again.",
                    Snackbar.LENGTH_LONG).show()
                Log.i(TAG, "An error occurred: $status")
            }

        })
    }

    private fun sendRequest() {
       val requestText = binding.content.request.text.toString()

        if (requestText.isEmpty()) {
            Snackbar.make(binding.layout,
                "Request cannot be empty",
                Snackbar.LENGTH_LONG).show()
        }
        else if (locality == null) {
            Snackbar.make(binding.layout,
                "Please enter your locality",
                Snackbar.LENGTH_LONG).show()
        } else {
            val request = Request(requestText, user.phone, user.name,
                FirebaseAuth.getInstance().currentUser!!.uid, locality!!)
            viewmodel.addRequest(request, FirebaseAuth.getInstance().currentUser!!.uid)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

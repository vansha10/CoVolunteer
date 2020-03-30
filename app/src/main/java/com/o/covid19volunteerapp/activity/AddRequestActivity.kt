package com.o.covid19volunteerapp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.o.covid19volunteerapp.BuildConfig
import com.o.covid19volunteerapp.databinding.ActivityAddRequestBinding
import com.o.covid19volunteerapp.model.Locality
import com.o.covid19volunteerapp.model.Request
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_add_request.*


class AddRequestActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddRequestBinding
    lateinit var viewmodel : FirebaseViewmodel
    lateinit var user : User
    val apiKey : String = BuildConfig.GOOGLE_MAP_KEY
    val TAG = "AddRequest"
    var locality: Locality? = null

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
        autocompleteFragment!!.setTypeFilter(TypeFilter.REGIONS)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS_COMPONENTS, Place.Field.ID))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                locality = null
                var postalCode : String? = null
                var country : String? = null
                val addressComponents = place.addressComponents!!.asList()
                for (a in addressComponents) {
                    if (a.types.contains("postal_code")) {
                        postalCode = a.name
                    }
                    if (a.types.contains("country")) {
                        country = a.name
                    }
                }
                if (postalCode == null || country == null) {
                    binding.content.selectedLocality.text = "Please enter a valid Postal Code"
                } else {
                    binding.content.selectedLocality.text = "You have selected: ${postalCode}"
                    locality = Locality(postalCode, country)
                    Log.i(TAG, "Place: $postalCode")
                }
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
                "Please enter your postal code",
                Snackbar.LENGTH_LONG).show()
        } else {
            showProgress()
            val request = Request(requestText, user.phone, user.name,
                FirebaseAuth.getInstance().currentUser!!.uid, locality!!)
            uploadRequest(request, FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }

    private fun uploadRequest(request: Request, uid: String) {
        val requestObserver = Observer<Boolean> { isSuccessful ->
            if (isSuccessful != null) {
                if (isSuccessful) {
                    Toast.makeText(this, "Request Added", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    hideProgress()
                    Snackbar.make(
                        binding.layout,
                        "Something went wrong. Please try again.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewmodel.addRequest(request, uid).observe(this, requestObserver)
    }

    override fun onBackPressed() {
        finish()
    }

    fun showProgress() {
        binding.content.layout.visibility = View.GONE
        binding.content.progress.visibility = View.VISIBLE
    }

    fun hideProgress() {
        binding.content.layout.visibility = View.VISIBLE
        binding.content.progress.visibility = View.GONE
    }
}

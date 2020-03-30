package com.o.covid19volunteerapp.activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.o.covid19volunteerapp.BuildConfig
import com.o.covid19volunteerapp.adapter.NeedRecyclerViewAdapter
import com.o.covid19volunteerapp.adapter.VolunteerRecyclerViewAdapter
import com.o.covid19volunteerapp.databinding.ActivityMainNeedBinding
import com.o.covid19volunteerapp.databinding.ActivityMainVolunteerBinding
import com.o.covid19volunteerapp.model.Locality
import com.o.covid19volunteerapp.model.Request
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.model.UserRequest
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_main_need.*
import kotlinx.android.synthetic.main.activity_main_need.recycler_view
import kotlinx.android.synthetic.main.activity_main_need.toolbar
import kotlinx.android.synthetic.main.activity_main_volunteer.*
import java.lang.NullPointerException
import java.util.*


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_FINE_LOCATION: Int = 1
    lateinit var bindingVolunteer: ActivityMainVolunteerBinding
    lateinit var bindingNeed: ActivityMainNeedBinding
    lateinit var viewmodel: FirebaseViewmodel
    var user: User? = null
    private var needRecyclerViewAdapter: NeedRecyclerViewAdapter? = null
    private var volunteerRecyclerViewAdapter : VolunteerRecyclerViewAdapter? = null
    private var userRequestsList: MutableList<UserRequest>? = null
    private var requestsList : MutableList<Request>? = null

    val apiKey: String = BuildConfig.GOOGLE_MAP_KEY
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        getUser()
    }

    private fun getUser() {
        val gson = Gson()
        user = gson.fromJson(intent.getStringExtra("user"), User::class.java)

        if (user!!.isVolunteer) {
            bindingVolunteer = DataBindingUtil.setContentView(
                this,
                com.o.covid19volunteerapp.R.layout.activity_main_volunteer
            )
            setSupportActionBar(toolbar)
            setupVolunteerUI()
        } else {
            bindingNeed = DataBindingUtil.setContentView(
                this,
                com.o.covid19volunteerapp.R.layout.activity_main_need
            )
            setSupportActionBar(toolbar)
            bindingNeed.fab.setOnClickListener { addRequest() }
            updateNeedUI()
        }
    }

    private fun setupVolunteerUI() {
        showProgress()
        getUserLocation()
    }

    private fun getUserLocation() {
        //TODO: add to repository

        // check that the user has granted permission
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (address != null && address.size > 0) {
                            val locality = Locality(address[0].postalCode, address[0].countryName)
                            bindingVolunteer.userPostalCode.text = "${locality.postalCode}, ${locality.country}"
                            getRequestsByLocation(locality)
                        }
                    }
                }
        } else {
            getLocationPermission()
        }
    }

    private fun getRequestsByLocation(locality: Locality) {
        val requestObserver = Observer<List<Request>> {requests ->
            if (requests != null) {
                updateVolunteerUI(requests)
            }
        }
        viewmodel.getRequestsByLocation(locality).observe(this, requestObserver)
    }

    private fun updateVolunteerUI(requests : List<Request>) {
        requestsList = requests as MutableList<Request>
        volunteerRecyclerViewAdapter = VolunteerRecyclerViewAdapter(requestsList!!) {request ->
            val intent = Intent(this, RequestInfoActivity::class.java)
            val gson = Gson()
            intent.putExtra("request", gson.toJson(request))
            intent.putExtra("user", gson.toJson(user))
            startActivity(intent)
        }

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = volunteerRecyclerViewAdapter
        }
        hideProgress()
    }

    private fun getLocationPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            showRequestDialog(this)
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
                this, arrayOf(ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getUserLocation()
                } else {
                    getLocationPermission()
                }
                return
            }
            else -> { }
        }
    }

    private fun updateNeedUI() {
        userRequestsList = user!!.requests as MutableList<UserRequest>
        needRecyclerViewAdapter = NeedRecyclerViewAdapter(userRequestsList!!)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = needRecyclerViewAdapter
        }
    }

    private fun addRequest() {
        val intent = Intent(this, AddRequestActivity::class.java)
        val gson = Gson()
        intent.putExtra("user", gson.toJson(this.user))
        startActivity(intent)
    }


    private fun hideProgress() {
        if (user != null) {
            if (user!!.isVolunteer)
                bindingVolunteer.progress.visibility = View.GONE
            else {
                bindingNeed.progress.visibility = View.GONE
            }
        }
    }

    private fun showProgress() {
        if (user != null) {
            if (user!!.isVolunteer)
                bindingVolunteer.progress.visibility = View.VISIBLE
            else {
                bindingNeed.progress.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.o.covid19volunteerapp.R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.o.covid19volunteerapp.R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    override fun onStart() {
        super.onStart()
        if (!user!!.isVolunteer)
            listenUserDataChange()
    }

    private fun listenUserDataChange() {
        val userDataChangeObserver = Observer<User> { user ->
            if (user != null) {
                needRecyclerViewAdapter?.updateList(user.requests as MutableList<UserRequest>)
            }
        }
        viewmodel.listenUserDataChange(FirebaseAuth.getInstance().currentUser!!.uid)
            .observe(this, userDataChangeObserver)
    }

    private fun showRequestDialog(activity: Activity) {
        this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("OK") { dialog, id ->
                    // User clicked OK button
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(
                        activity, arrayOf(ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_FINE_LOCATION
                    )
                }
            }
            builder.setMessage(
                "Please allow location permission to continue.\n" +
                        "We need your location to find people in need near you."
            )
                .setTitle("Location Permission")
            builder.create()
        }
    }
}


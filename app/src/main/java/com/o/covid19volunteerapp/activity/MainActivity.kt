package com.o.covid19volunteerapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.o.covid19volunteerapp.adapter.NeedRecyclerViewAdapter
import com.o.covid19volunteerapp.databinding.ActivityMainNeedBinding
import com.o.covid19volunteerapp.databinding.ActivityMainVolunteerBinding
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.model.UserRequest
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_main_need.*


class MainActivity : AppCompatActivity() {

    lateinit var bindingVolunteer : ActivityMainVolunteerBinding
    lateinit var bindingNeed : ActivityMainNeedBinding
    lateinit var viewmodel : FirebaseViewmodel
    var user : User? = null
    lateinit var recyclerViewAdapter : NeedRecyclerViewAdapter
    lateinit var userRequestsList : MutableList<UserRequest>

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
            bindingVolunteer = DataBindingUtil.setContentView(this,
                com.o.covid19volunteerapp.R.layout.activity_main_volunteer)
            setSupportActionBar(toolbar)
        } else {
            bindingNeed = DataBindingUtil.setContentView(this,
                com.o.covid19volunteerapp.R.layout.activity_main_need)
            setSupportActionBar(toolbar)
            bindingNeed.fab.setOnClickListener { addRequest() }
            updateNeedUI()
        }
    }

    private fun updateNeedUI() {
        userRequestsList = user!!.requests as MutableList<UserRequest>
        recyclerViewAdapter = NeedRecyclerViewAdapter(userRequestsList)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
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
        listenUserDataChange()
    }

    private fun listenUserDataChange() {
        val userDataChangeObserver = Observer<User> {user ->
            if (user != null) {
                recyclerViewAdapter.updateList(user.requests as MutableList<UserRequest>)
            }
        }
        viewmodel.listenUserDataChange(FirebaseAuth.getInstance().currentUser!!.uid)
            .observe(this, userDataChangeObserver)
    }
}

package com.o.covid19volunteerapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.o.covid19volunteerapp.databinding.ActivityMainLoadingBinding
import com.o.covid19volunteerapp.databinding.ActivityMainNeedBinding
import com.o.covid19volunteerapp.databinding.ActivityMainVolunteerBinding
import com.o.covid19volunteerapp.model.User
import com.o.covid19volunteerapp.viewmodel.FirebaseViewmodel
import kotlinx.android.synthetic.main.activity_main_loading.*
import kotlinx.android.synthetic.main.activity_main_loading.toolbar
import kotlinx.android.synthetic.main.activity_main_need.*
import kotlinx.android.synthetic.main.activity_main_volunteer.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    lateinit var bindingVolunteer : ActivityMainVolunteerBinding
    lateinit var bindingNeed : ActivityMainNeedBinding
    lateinit var viewmodel : FirebaseViewmodel
    var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bindingLoading = DataBindingUtil.setContentView(this, com.o.covid19volunteerapp.R.layout.activity_main_loading)

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewmodel::class.java)
        viewmodel.init()

        getUser()

        setSupportActionBar(toolbar)
    }

    private fun getUser() {
        val uid = intent.getStringExtra("uid")

        val loginUserObserver = Observer<User> { user ->
            if (user != null) {
                this.user = user
                if (user.isVolunteer) {
                    bindingVolunteer = DataBindingUtil.setContentView(this,
                        com.o.covid19volunteerapp.R.layout.activity_main_volunteer)
                    setSupportActionBar(toolbar)
                } else {
                    bindingNeed = DataBindingUtil.setContentView(this,
                        com.o.covid19volunteerapp.R.layout.activity_main_need)
                    setSupportActionBar(toolbar)
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        viewmodel.getUserData(uid!!).observe(this, loginUserObserver)
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
}

package com.o.covid19volunteerapp.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.databinding.ActivitySignupBinding
import com.o.covid19volunteerapp.model.User
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*
import java.util.regex.Pattern


class SignupActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener{

    private var volunteer: Boolean? = null
    private lateinit var binding : ActivitySignupBinding
    private var date : Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        setSupportActionBar(toolbar)

        val datePickerDialog = DatePickerDialog(this, this,
            2000, 0, 1)

        binding.content.dob.setOnClickListener { datePickerDialog.show() }

        fab.setOnClickListener { view ->
            validateInput()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun validateInput() {
        val name = binding.content.name.text.toString()
        val countryCode = binding.content.ccp.selectedCountryCodeWithPlus
        val phoneNumber = binding.content.phone.text.toString()
        val email = binding.content.email.text.toString()
        val password = binding.content.password.text.toString()
        val rePassword = binding.content.rePassword.text.toString()
        val dob = date

        var focusView : View? = null

        if (name.isEmpty()) {
            focusView = binding.content.name
            binding.content.name.error = "Please enter your name"
        }
        if (!isValidMobile(phoneNumber)) {
            if (focusView == null)
                focusView = binding.content.phone
            binding.content.phone.error = "Please enter a valid phone number"
        }
        if (!isValidMail(email)) {
            if (focusView == null)
                focusView = binding.content.email
            binding.content.email.error = "Please enter a valid email address"
        }
        if (password.length < 8) {
            if (focusView == null)
                focusView = binding.content.password
            binding.content.password.error = "Password should have at least 8 characters"
        }
        if (password != rePassword) {
            if (focusView == null)
                focusView = binding.content.rePassword
            binding.content.rePassword.error = "Passwords don't match"
        }
        if (dob == null) {
            if (focusView == null)
                focusView = binding.content.dob
            binding.content.dob.error = "Please enter your Date of Birth"
        }
        if (volunteer == null) {
            if (focusView == null)
                focusView = binding.content.radioHeading
            binding.content.radioHeading.error = "Please select an option"
        }
        if (focusView == null) {
            val user = User(name, countryCode + phoneNumber, email, dob!!, volunteer!!)
            val gson = Gson()
            val intent = Intent(this, PhoneVerificationActivity::class.java)
            intent.putExtra("user", gson.toJson(user))
            intent.putExtra("password", password)
            startActivity(intent)
        } else {
            focusView.requestFocus()
        }
    }

    private fun isValidMail(email: String): Boolean {

        val EMAIL_STRING =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

        return Pattern.compile(EMAIL_STRING).matcher(email).matches()

    }

    private fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length in 7..13
        } else false
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        binding.content.dob.error = null
        date = Calendar.getInstance()
        date!!.set(p1, p2, p3)
        val dateString : String = String.format("%d/%d/%d", date!!.get(Calendar.DATE),
            (date!!.get(Calendar.MONTH) + 1), date!!.get(Calendar.YEAR))
        binding.content.dob.setText(dateString)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            binding.content.radioHeading.error = null

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_volunteer ->
                    if (checked) {
                        volunteer = true
                    }
                R.id.radio_needs_help ->
                    if (checked) {
                        volunteer = false
                    }
            }
        }
    }

}

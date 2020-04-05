package com.o.covid19volunteerapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.o.covid19volunteerapp.adapter.ResponseRecyclerViewAdapter
import com.o.covid19volunteerapp.databinding.ActivityUserRequestInfoBinding
import com.o.covid19volunteerapp.model.Response
import com.o.covid19volunteerapp.model.UserRequest
import kotlinx.android.synthetic.main.activity_main_need.*
import kotlinx.android.synthetic.main.activity_user_request_info.toolbar


class UserRequestInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityUserRequestInfoBinding
    lateinit var userRequest: UserRequest
    lateinit var recyclerViewAdapter: ResponseRecyclerViewAdapter
    lateinit var responseList: MutableList<Response>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.o.covid19volunteerapp.R.layout.activity_user_request_info)
        setSupportActionBar(toolbar)

        getExtras()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getExtras() {
        val gson = Gson()
        userRequest = gson.fromJson(intent.getStringExtra("userRequest"), UserRequest::class.java)
        initUi()
    }

    private fun initUi() {
        binding.content.locality.text = "${userRequest.locality.postalCode}, ${userRequest.locality.country}"
        binding.content.text.text = userRequest.requestText

        responseList = userRequest.responses
        recyclerViewAdapter = ResponseRecyclerViewAdapter(responseList) {response ->
            showDialog(response)
            //val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response.phoneNumber))
            //startActivity(intent)
        }
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
    }

    private fun showDialog(response: Response) {
        this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("CALL") { dialog, id ->
                    dialog.dismiss()
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response.phoneNumber))
                    startActivity(intent)
                }
                setNegativeButton("CANCEL") {dialog, id ->
                    dialog.dismiss()
                }
            }
            builder.setMessage(
                "You are about to call a volunteer.\n" +
                        "Before giving your address to the volunteer, please ensure the authenticity of the volunteer.\n" +
                        "For your safety, instruct the volunteer to leave your goods outside your home and " +
                        "avoid direct contact with them."
            )
                .setTitle("Call a volunteer")
            builder.create()
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
    }

}

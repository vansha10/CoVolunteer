package com.o.covid19volunteerapp.model

import java.util.*

data class User (var name : String, var phone : String, var email :String, var dob : Long, var isVolunteer : Boolean) {
    constructor() : this("", "", "", 0, true)
}
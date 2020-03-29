package com.o.covid19volunteerapp.model

data class User (var name : String, var phone : String, var email :String, var dob : Long,
                 var isVolunteer : Boolean, var requests : List<UserRequest>) {
    constructor() : this("", "", "", 0, true,
        List<UserRequest>(0) { UserRequest() })
    constructor(name : String, phone : String, email : String, dob : Long, isVolunteer: Boolean) : this(name,
        phone, email, dob, isVolunteer, List<UserRequest>(0) { UserRequest() })
}
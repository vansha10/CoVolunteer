package com.o.covid19volunteerapp.model

data class Response(var name : String, var phoneNumber : String, var uid : String) {
    constructor() : this ("", "", "")
}
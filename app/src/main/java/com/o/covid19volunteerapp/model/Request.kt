package com.o.covid19volunteerapp.model

data class Request (var id : String, var requestText: String, var phoneNumber : String, var name : String,
                    var uid : String, var locality: Locality) {
    constructor() : this("","", "", "", "", Locality())
    constructor(requestText: String, phoneNumber: String, name: String, uid: String, locality: Locality) : this("", requestText,
        phoneNumber, name, uid, locality)
}
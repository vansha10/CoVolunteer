package com.o.covid19volunteerapp.model

data class Locality(var postalCode : String, var country : String) {
    constructor() : this("", "")
}
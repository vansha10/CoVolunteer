package com.o.covid19volunteerapp.model
/**
 Model for request put up by a user, stored inside user data
 **/
data class UserRequest(var requestText: String, var locality : String, var requestId : String, var responses : List<Response>) {
    constructor() : this ("", "", "", List<Response>(0) { Response() })
    fun setRequest(request : Request) {
        this.requestText = request.requestText
        this.locality = request.locality
    }
}
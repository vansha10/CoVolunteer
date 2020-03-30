package com.o.covid19volunteerapp.model
/**
 Model for request put up by a user, stored inside user data
 **/
data class UserRequest(var requestText: String, var locality: Locality, var requestId: String, var responses: MutableList<Response>) {
    constructor() : this ("", Locality(), "", MutableList<Response>(0) { Response() })
    fun setRequest(request : Request) {
        this.requestText = request.requestText
        this.locality = request.locality
    }
}
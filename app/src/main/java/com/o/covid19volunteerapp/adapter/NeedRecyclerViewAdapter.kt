package com.o.covid19volunteerapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.model.UserRequest

class NeedRecyclerViewAdapter(private var list: MutableList<UserRequest>)
    : RecyclerView.Adapter<NeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NeedViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: NeedViewHolder, position: Int) {
        val request : UserRequest = list[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList : MutableList<UserRequest>) {
        list.clear()
        list.addAll(newList)
        this.notifyDataSetChanged()
    }
}

class NeedViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.need_list_item, parent, false)) {
    private var postalCodeTextView : TextView? = null
    private var requestTextView : TextView? = null
    private var countTextView : TextView? = null


    init {
        postalCodeTextView = itemView.findViewById(R.id.request_locality)
        requestTextView = itemView.findViewById(R.id.request_text)
        countTextView = itemView.findViewById(R.id.request_response_count)
    }

    fun bind(request: UserRequest) {
        postalCodeTextView?.text = "${request.locality.postalCode}, ${request.locality.country}"
        requestTextView?.text = request.requestText
        countTextView?.text = request.responses.size.toString()
    }

}
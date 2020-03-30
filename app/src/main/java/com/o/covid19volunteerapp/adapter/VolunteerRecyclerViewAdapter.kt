package com.o.covid19volunteerapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.model.Request
import com.o.covid19volunteerapp.model.UserRequest

class VolunteerRecyclerViewAdapter(private var list: MutableList<Request>, val clickListener: (Request) -> Unit)
    : RecyclerView.Adapter<VolunteerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return VolunteerViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val request : Request = list[position]
        holder.bind(request)
        holder.itemView.setOnClickListener {
            clickListener(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList : MutableList<Request>) {
        list.clear()
        list.addAll(newList)
        this.notifyDataSetChanged()
    }
}

class VolunteerViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.volunteer_list_item, parent, false)) {
    private var nameCodeTextView : TextView? = null
    private var requestTextView : TextView? = null


    init {
        nameCodeTextView = itemView.findViewById(R.id.request_name)
        requestTextView = itemView.findViewById(R.id.request_text)
    }

    fun bind(request: Request) {
        nameCodeTextView?.text = request.name
        requestTextView?.text = request.requestText
    }

}
package com.o.covid19volunteerapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.o.covid19volunteerapp.R
import com.o.covid19volunteerapp.model.Response

class ResponseRecyclerViewAdapter(private var list: MutableList<Response>,  val clickListener: (Response) -> Unit)
    : RecyclerView.Adapter<ResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ResponseViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        val response : Response = list[position]
        holder.bind(response)
        holder.callButton?.setOnClickListener {
            clickListener(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList : MutableList<Response>) {
        list.clear()
        list.addAll(newList)
        this.notifyDataSetChanged()
    }
}

class ResponseViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.response_list_item, parent, false)) {
    private var nameTextView : TextView? = null
    public var callButton: ImageView? = null


    init {
        nameTextView = itemView.findViewById(R.id.name)
        callButton = itemView.findViewById(R.id.call_button)
    }

    fun bind(response: Response) {
        nameTextView?.text = response.name
    }

}
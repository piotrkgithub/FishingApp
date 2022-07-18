package com.example.fishingapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingapp.activities.AddEventActivity
import com.example.fishingapp.database.EventDatabaseHandler
import com.example.fishingapp.activities.EventsListActivity
import com.example.fishingapp.R
import com.example.fishingapp.models.EventModel
import kotlinx.android.synthetic.main.item_event.view.*

class EventAdapter (
    private val context: Context,
    private var list: ArrayList<EventModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_event,
                parent,
                false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
       this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.itemView.tvTitleEvent.text = model.date
            holder.itemView.tvLocationEvent.text ="Miejsce: " + model.location
            if(model.description == ""){
                holder.itemView.tvTimeEvent.text = "Uwagi: brak"
            }else{
                holder.itemView.tvTimeEvent.text = "Uwagi: " + model.description
            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    fun removeAt(position: Int){
        val dbHandler = EventDatabaseHandler(context)
        val isDeleted = dbHandler.deleteEvent(list[position])
        if(isDeleted > 0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun notifyEditItem(activity: Activity, position : Int, requestCode: Int){
        val intent = Intent(context, AddEventActivity::class.java)
        intent.putExtra(EventsListActivity.EXTRA_EVENT_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: EventModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
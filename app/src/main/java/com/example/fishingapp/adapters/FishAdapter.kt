package com.example.fishingapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingapp.*
import com.example.fishingapp.activities.AddFishActivity
import com.example.fishingapp.activities.FishListActivity
import com.example.fishingapp.database.FishDatabaseHandler
import com.example.fishingapp.models.FishModel
import kotlinx.android.synthetic.main.item_fish.view.*

open class FishAdapter(
    private val context: Context,
    private var list: ArrayList<FishModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_fish,
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
            holder.itemView.tvTitleFish.text = model.title
            holder.itemView.tvTimeFish.text = model.date
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    fun removeAt(position: Int){
        val dbHandler = FishDatabaseHandler(context)
        val isDeleted = dbHandler.deleteFish(list[position])
        if(isDeleted > 0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddFishActivity::class.java)
        intent.putExtra(FishListActivity.EXTRA_FISH_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: FishModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
package com.example.fishingapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingapp.models.PostModel

class PostAdapter (
    val postModel: PostModel
): RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.card_post,
            parent,
            false
        )
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        return holder.bindView(postModel)
    }

    override fun getItemCount(): Int {
        return 1
    }
}

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sunMainText: TextView = itemView.findViewById(R.id.sunMainText)
    private val sunRiseText: TextView = itemView.findViewById(R.id.sunRiseText)
    private val sunRise: TextView = itemView.findViewById(R.id.sunRise)
    private val sunTransitText: TextView = itemView.findViewById(R.id.sunTransitText)
    private val sunTransit: TextView = itemView.findViewById(R.id.sunTransit)
    private val sunSetText: TextView = itemView.findViewById(R.id.sunSetText)
    private val sunSet: TextView = itemView.findViewById(R.id.sunSet)
    private val moonMainText: TextView = itemView.findViewById(R.id.moonMainText)
    private val moonRiseText: TextView = itemView.findViewById(R.id.moonRiseText)
    private val moonRise: TextView = itemView.findViewById(R.id.moonRise)
    private val moonTransitText: TextView = itemView.findViewById(R.id.moonTransitText)
    private val moonTransit: TextView = itemView.findViewById(R.id.moonTransit)
    private val moonSetText: TextView = itemView.findViewById(R.id.moonSetText)
    private val moonSet: TextView = itemView.findViewById(R.id.moonSet)
    private val moonPhaseText: TextView = itemView.findViewById(R.id.moonPhaseText)
    private val moonPhase: TextView = itemView.findViewById(R.id.moonPhase)
    private val majorText: TextView = itemView.findViewById(R.id.majorText)
    private val major1Start: TextView = itemView.findViewById(R.id.major1Start)
    private val major1Stop: TextView = itemView.findViewById(R.id.major1Stop)
    private val major2Start: TextView = itemView.findViewById(R.id.major2Start)
    private val major2Stop: TextView = itemView.findViewById(R.id.major2Stop)
    private val minorText: TextView = itemView.findViewById(R.id.minorText)
    private val minor1Start: TextView = itemView.findViewById(R.id.minor1Start)
    private val minor1Stop: TextView = itemView.findViewById(R.id.minor1Stop)
    private val minor2Start: TextView = itemView.findViewById(R.id.minor2Start)
    private val minor2Stop: TextView = itemView.findViewById(R.id.minor2Stop)
    private val dayRatingText: TextView = itemView.findViewById(R.id.dayRatingText)
    private val dayRating: TextView = itemView.findViewById(R.id.dayRating)

    @SuppressLint("SetTextI18n")
    fun bindView(postModel: PostModel) {
        sunMainText.text = "Słońce: "
        sunRiseText.text = "Wschód: "
        sunRise.text = postModel.sunRise
        sunTransitText.text = "Kulminacja: "
        sunTransit.text = postModel.sunTransit
        sunSetText.text = "Zachód: "
        sunSet.text = postModel.sunSet
        moonMainText.text = "Księżyc"
        moonRiseText.text = "Wschód: "
        moonRise.text = postModel.moonRise
        moonTransitText.text = "Kulminacja: "
        moonTransit.text = postModel.moonTransit
        moonSetText.text = "Zachód: "
        moonSet.text = postModel.moonSet
        moonPhaseText.text = "Faza księżyca"
        moonPhase.text = postModel.moonPhase
        majorText.text = "Okres największej aktywności ryb:"
        major1Start.text = "Od " + postModel.major1Start
        major1Stop.text = " do " + postModel.major1Stop
        major2Start.text = "Od " + postModel.major2Start
        major2Stop.text = " do " + postModel.major2Stop
        minorText.text = "Okresy mniejszej aktywności ryb:"
        minor1Start.text = "Od " + postModel.minor1Start
        minor1Stop.text = " do " + postModel.minor1Stop
        minor2Start.text = "Od " + postModel.minor2Start
        minor2Stop.text = " do " + postModel.minor2Stop
        dayRatingText.text = "Średnia aktywność ryb:"
        if(postModel.dayRating == "0"){
            dayRating.text = "bardzo słaba"
        }else if(postModel.dayRating == "1"){
            dayRating.text = "słaba"
        }else if(postModel.dayRating == "2"){
            dayRating.text = "średnia"
        }else if(postModel.dayRating == "3"){
            dayRating.text = "dobra"
        }else if(postModel.dayRating == "4"){
            dayRating.text = "bardzo dobra"
        }else if(postModel.dayRating == "5"){
            dayRating.text = "świetna"
        }else{
            dayRating.text = "świetna"
        }
    }
}
package com.example.fishingapp.activities

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fishingapp.R
import com.example.fishingapp.models.FishModel
import kotlinx.android.synthetic.main.activity_fish_detail.*

class FishDetailActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_detail)

        var fishDetailModel : FishModel? = null

        if(intent.hasExtra(FishListActivity.EXTRA_FISH_DETAILS)){
            fishDetailModel = intent.getSerializableExtra(FishListActivity.EXTRA_FISH_DETAILS) as FishModel
        }

        if(fishDetailModel != null){
            setSupportActionBar(toolbar_fish_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = fishDetailModel.title

            toolbar_fish_detail.setNavigationOnClickListener {
                onBackPressed()
            }

            iv_place_image.setImageURI(Uri.parse(fishDetailModel.image))
            tv_description.text = "Gatunek: " + fishDetailModel.description
            tv_location.text = "Miejsce: " + fishDetailModel.location
            tv_date.text = "Data połowu: " + fishDetailModel.date
            tv_length.text = "Długość: " + fishDetailModel.length + " cm"
            tv_weight.text = "Waga: " + fishDetailModel.weight + " kg"
        }
    }
}
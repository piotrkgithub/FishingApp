package com.example.fishingapp.models

import java.io.Serializable

data class FishModel (
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val length: String,
    val weight: String
): Serializable
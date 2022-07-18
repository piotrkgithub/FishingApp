package com.example.fishingapp.models

import java.io.Serializable

data class EventModel (
    val id: Int,
    val date: String,
    val location: String,
    val description: String
): Serializable
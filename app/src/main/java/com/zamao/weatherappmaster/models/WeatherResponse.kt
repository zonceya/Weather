package com.weatherappmaster.models

import com.weatherappmaster.models.Wind
import java.io.Serializable

// TODO (STEP 3: Create a data model class for using it for the api response. And also create all the models used in this model class.)
// START
data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Int,
    val sys: Sys,
    val id: Int,
    val name: String,
    val cod: Int
) : Serializable
// END
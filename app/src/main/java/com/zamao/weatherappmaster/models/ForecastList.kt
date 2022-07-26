package com.zamao.weatherappmaster.models

import com.weatherappmaster.models.*
import java.io.Serializable


data class ForecastList(
    val dt: Int,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val sys: Sys,
    val dtTxt: Dt_txt
): Serializable
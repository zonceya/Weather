package com.zamao.weatherappmaster.models
import com.weatherappmaster.models.*
import java.io.Serializable
import kotlin.collections.List

data class ForecastResponse (
    val cod:Long,
    val message: Double,
    val cnt:Int,
    val list: List<ForecastList>,
    val coord: Coord,
    val city: City,
    val country: String
 ) : Serializable

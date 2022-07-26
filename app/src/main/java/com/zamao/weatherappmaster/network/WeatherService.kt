package com.weatherappmaster.network


import com.weatherappmaster.models.WeatherResponse
import com.zamao.weatherappmaster.models.ForecastResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// TODO (STEP 4: Create a WeatherService interface)
// START
/**
 * An Interface which defines the HTTP operations Functions.
 */
interface WeatherService {

    @GET("2.5/weather")
    fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("units") units: String?,
            @Query("appid") appid: String?
    ): Call<WeatherResponse>
    @GET("2.5/forecast")
    fun getHistoryData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?,
        @Query("cnt") cnt: String?,
        @Query("appid") appid: String?
    ): Call<ForecastResponse>
}
// END
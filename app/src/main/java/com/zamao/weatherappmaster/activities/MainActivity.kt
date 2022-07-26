package com.zamao.weatherappmaster.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.weatherapp.utils.Constants
import com.weatherappmaster.models.WeatherResponse
import com.weatherappmaster.network.WeatherService
import com.zamao.weatherappmaster.R
import com.zamao.weatherappmaster.models.ForecastList
import com.zamao.weatherappmaster.models.ForecastResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


// OpenWeather Link : https://openweathermap.org/api
/**
 * The useful link or some more explanation for this app you can checkout this link :
 * https://medium.com/@sasude9/basic-android-weather-app-6a7c0855caf4
 */
class MainActivity : AppCompatActivity() {
    private val itemsList = ArrayList<ForecastList>()
    private lateinit var customAdapter: CustomAdapter
    // A fused location client variable which is further user to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mProgressDialog: Dialog? = null
    private lateinit var  forecastSharedPreferences : SharedPreferences
    private lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerview.layoutManager = layoutManager

        recyclerview.adapter = customAdapter
     //   val myDataset = Datasource().Images()
        loadImage()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        forecastSharedPreferences = getSharedPreferences(
            Constants.FORECAST_NAME,
            Context.MODE_PRIVATE
        )
        setupUI()
        setupForecastUI()
        if (!isLocationEnabled()) {
            Toast.makeText(
                this,
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()

            // This will redirect you to settings from where you need to turn on the location provider.
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestLocationData()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have denied location permission. Please allow it is mandatory.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?,
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }

    }
    private fun loadImage(){
        val list: MutableList<LoadImage> = mutableListOf()
        list.add(LoadImage("02d" ,(R.drawable.partlysunny)))
        list.add(LoadImage("01d" ,(R.drawable.clear)))
        list.add(LoadImage("02d" ,(R.drawable.partlysunny)))
        list.add(LoadImage("03d" ,(R.drawable.partlysunny)))
        list.add(LoadImage("04d" ,(R.drawable.partlysunny)))
        list.add(LoadImage("04n" ,(R.drawable.partlysunny)))
        list.add(LoadImage("10d" ,(R.drawable.rain)))
        list.add(LoadImage("11d" ,(R.drawable.partlysunny)))
        list.add(LoadImage("13d" ,(R.drawable.partlysunny)))
        list.add(LoadImage("01n" ,(R.drawable.partlysunny)))
        list.add(LoadImage("02n" ,(R.drawable.partlysunny)))
        list.add(LoadImage("03n" ,(R.drawable.partlysunny)))
        list.add(LoadImage("10n" ,(R.drawable.partlysunny)))
        list.add(LoadImage("11n" ,(R.drawable.rain)))
        list.add(LoadImage("13n" ,(R.drawable.partlysunny)))

        customAdapter.setData(list)
    }
    private fun prepareItems() {

        customAdapter.notifyDataSetChanged()
    }
    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }
    @SuppressLint("ResourceType")
    private fun setupUI() {
        val weatherResponseJsonString =
            mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")
        if (!weatherResponseJsonString.isNullOrEmpty()) {
            var weatherList =
                Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
            for (z in weatherList.weather.indices) {
                var tv_main = findViewById<TextView>(R.id.tv_main)
                var tv_temp = findViewById<TextView>(R.id.tv_temp)
                var tv_current = findViewById<TextView>(R.id.tv_current)
                var tv_humidity = findViewById<TextView>(R.id.tv_humidity)
                var tv_min = findViewById<TextView>(R.id.tv_min)
                var tv_max = findViewById<TextView>(R.id.tv_max)
                var tv_name = findViewById<TextView>(R.id.tv_date)
               if( weatherList.weather.contains("Rain")){
                   var zama = 0
               }

                if (weatherList.weather[z].main == "Rain")
                {
                    var   relativeLayout = findViewById<LinearLayout>(R.id.rl)
                    relativeLayout.setBackgroundResource(R.drawable.forest_rainy)
                }
                if (weatherList.weather[0].main == "Clear")
                {
                    var   relativeLayout = findViewById<LinearLayout>(R.id.rl)
                    relativeLayout.setBackgroundResource(R.drawable.forest_sunny)
                   var   color = findViewById<LinearLayout>(R.id.color1)
                   color.setBackgroundColor(Color.parseColor("#54717A"))
                    var   recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
                    recyclerview.setBackgroundColor(Color.parseColor("#54717A"))

                }

                if (weatherList.weather[0].main == "Sunny")
                {
                    var   relativeLayout = findViewById<LinearLayout>(R.id.rl)
                    relativeLayout.setBackgroundResource(R.drawable.forest_sunny)
                }
                if (weatherList.weather[0].main.toString()== "Clouds")
                {
                    var   relativeLayout = findViewById<LinearLayout>(R.id.rl)
                    relativeLayout.setBackgroundResource(R.drawable.forest_cloudy)
                }
                Log.i("NAMEEEEEEEE", weatherList.weather[z].main)
                tv_main.text = weatherList.weather[z].main
                var iv_main = findViewById<ImageView>(R.id.iv_main)
                //tv_main_description.text = weatherList.weather[z].description
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    tv_temp.text =
                        weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                    tv_min.text = weatherList.main.temp_min.toString() + getUnit(application.resources.configuration.locales.toString()) +" \n min"
                    tv_max.text = weatherList.main.temp_max.toString()+ getUnit(application.resources.configuration.locales.toString()) + "\n max"
                    tv_current.text =    weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString()) + "\n current"
                }
                tv_humidity.text = weatherList.main.humidity.toString() + " per cent"

                // Here we update the main icon
                when (weatherList.weather[z].icon) {
                    "02d" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "01d" -> iv_main.setImageResource(R.drawable.clear)
                    "02d" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "03d" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "04d" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "04n" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "10d" -> iv_main.setImageResource(R.drawable.rain)
                    "11d" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "13d" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "01n" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "02n" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "03n" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "10n" -> iv_main.setImageResource(R.drawable.partlysunny)
                    "11n" -> iv_main.setImageResource(R.drawable.rain)
                    "13n" -> iv_main.setImageResource(R.drawable.partlysunny)
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setupForecastUI() {
        // prepareItems()
        var tv_temp = findViewById<TextView>(R.id.tv_temp)
        var tv_current = findViewById<TextView>(R.id.tv_current)
       // var iv_main = findViewById<ImageView>(R.id.iv_main)


        var forecastResponseJsonString =
            forecastSharedPreferences.getString(Constants.FORECAST_RESPONSE_DATA, "")

        if (!forecastResponseJsonString.isNullOrEmpty()) {

            var forecastList =
                Gson().fromJson(forecastResponseJsonString, ForecastResponse::class.java)

            for (z in forecastList.list.slice(0..4)) {

                var agent: MutableList<ForecastList> = mutableListOf()
                //  val hj = unixTime(z.dt.toLong())

                forecastList.list.forEach {

                    agent.addAll(listOf(it))
                    itemsList.addAll(listOf(it))
                }


                prepareItems()

                val weatherIcon = z.weather
                weatherIcon.forEach{
                    val setupIcon = weatherIcon[0].icon
                    when(setupIcon){

                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    tv_current.text =    z.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                    tv_temp.text =    z.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                   // iv_main.text =    z.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                }

            }
        }
    }
    private fun getUnit(value: String): String? {
        Log.i("unitttttt", value)
        var value = "°"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "°F"
        }
        return value
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                requestLocationData()
                true
            }
            else -> super.onOptionsItemSelected(item)
            // END
        }
    }
    /**
     * The function is used to get the formatted time based on the Format and the LOCALE we pass to it.
     */
    private fun unixTime(timex: Long): String? {

        val sdf = java.text.SimpleDateFormat("EEEE")
        val date = java.util.Date(timex * 1000)
        return sdf.format(date )
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") {
                    dialog,
                    _,
                ->
                dialog.dismiss()
            }.show()
    }


    @SuppressLint("MissingPermission")
    private fun requestLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }


    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val latitude = mLastLocation.latitude
            Log.i("Current Latitude", "$latitude")

            val longitude = mLastLocation.longitude
            Log.i("Current Longitude", "$longitude")
            getLocationWeatherDetails(latitude, longitude)
            getLocationForecastDetails(latitude, longitude)
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {

        if (Constants.isNetworkAvailable(this@MainActivity)) {

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService =
                retrofit.create(WeatherService::class.java)

            val listCall:
                    Call<WeatherResponse> = service.getWeather(
                latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID
            )
            showCustomProgressDialog()

            listCall.enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {

                    if (response.isSuccessful) {
                        hideProgressDialog()
                        val weatherList: WeatherResponse? = response.body()
                        val weatherResponseJsonString = Gson().toJson(weatherList)

                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()
                        Log.i("Response Result", "$weatherList")
                        if (weatherList != null) {
                            setupUI()
                        }
                    } else {
                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }


                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Errorrrrr", t.message.toString())
                }

            })
            // END

        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getLocationForecastDetails(latitude: Double, longitude: Double) {

        if (Constants.isNetworkAvailable(this@MainActivity)) {

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService =
                retrofit.create(WeatherService::class.java)

            val listForecastCall:
                    Call<ForecastResponse> = service.getHistoryData(
                latitude, longitude, Constants.METRIC_UNIT, Constants.CNT, Constants.APP_ID
            )


            listForecastCall.enqueue(object : Callback<ForecastResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>
                ) {

                    if (response.isSuccessful) {
                        hideProgressDialog()
                        val forecastList: ForecastResponse? = response.body()
                        val forecatsResponseJsonString = Gson().toJson(forecastList)

                        val editor = forecastSharedPreferences.edit()
                        editor.putString(
                            Constants.FORECAST_RESPONSE_DATA,
                            forecatsResponseJsonString
                        )
                        editor.apply()
                        Log.i("Response Result", "$forecastList")
                        if (forecastList != null) {
                            setupForecastUI()
                        }
                    } else {
                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }


                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    Log.e("Errorrrrr", t.message.toString())
                }


            })


        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}


package com.zamao.weatherappmaster.activities


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.zamao.weatherappmaster.R
import com.zamao.weatherappmaster.models.ForecastList
import java.util.*


internal class CustomAdapter(private var itemsList: ArrayList<ForecastList>) :    RecyclerView.Adapter<CustomAdapter.MyViewHolder>()
{
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.textView)
        var imageview: ImageView = view.findViewById(R.id.imageview)
        var itemTextDay: TextView = view.findViewById(R.id.textDay)


    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleview, parent, false)
        return MyViewHolder(itemView)

    }
    private val loadImage: MutableList<LoadImage> = mutableListOf()

    fun setData(list: List<LoadImage>) {
        loadImage.clear()
        loadImage.addAll(list)
        loadImage.joinToString()
    }

    fun WeatherDays(): List<Int> = itemsList.map { it -> it.dt }
    var previousDay =""
       override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]

           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

               holder.itemTextDay.text =   item.main.temp.toString() + getUnit(toString())
           }

            var current = unixTime(item.dt.toLong())
           if(previousDay != current.toString()){
               var yes = true
           }
           loadImage.forEach { it -> if(it.name == item.weather[0].icon)
{
    holder.imageview.setImageResource(it.image)
  }
}
    }
    override fun getItemCount(): Int {
        return itemsList.size
    }
    private fun unixTime(timex: Long): String? {
        val sdf = java.text.SimpleDateFormat("EEEE")
        val date = java.util.Date(timex * 1000)
         previousDay = sdf.format(date)
        return sdf.format(date)
    }
    private fun getUnit(value: String): String? {
        var value = "°"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "°F"
        }
        return value
    }
}

 class WeatherDays {
     val agentId : Int = 0

 }

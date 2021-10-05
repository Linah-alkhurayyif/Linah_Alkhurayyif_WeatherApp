package com.example.linah_alkhurayyif_weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val apiId = "8a316bae40ca552c86771c6d73150592"
    var city = "Seoul"
    lateinit var City_TV:TextView
    lateinit var Updated_TV:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         City_TV = findViewById<TextView>(R.id.CityText)
         Updated_TV = findViewById<TextView>(R.id.UpdatedText)
        var wither_TV = findViewById<TextView>(R.id.witherText)
        var Temperature_TV = findViewById<TextView>(R.id.Temperature)
        var LowTemperature_TV = findViewById<TextView>(R.id.LowTemperature)
        var HighTemperature_TV = findViewById<TextView>(R.id.HighTemperature)
        request_API()
    }
    private fun request_API(){
        CoroutineScope(IO).launch {
            val data = async {
                fetch_WeatherData()
            }.await()
            if(data.isNotEmpty()){
                getWeatherData(data)
            }
        }
    }
    private fun fetch_WeatherData(): String{
        var response = ""
        try {
            response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiId")
                .readText(Charsets.UTF_8)
        }catch (e: Exception){
            println("Something wrong: $e")
        }
        return response
    }
    private suspend fun getWeatherData(result: String){
        withContext(Main){
            val jsonObj = JSONObject(result)
            val name = jsonObj.getString("name")
            val sys = jsonObj.getJSONObject("sys")
            val country = sys.getString("country")
            val lastUpdate:Long = jsonObj.getLong("dt")
            City_TV.text ="$name, $country"
            Updated_TV.text = "Updated at: " + SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.ENGLISH).format(Date(lastUpdate*1000))

        }
    }

}
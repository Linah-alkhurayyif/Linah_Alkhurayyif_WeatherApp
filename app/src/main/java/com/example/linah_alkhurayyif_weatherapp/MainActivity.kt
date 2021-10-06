package com.example.linah_alkhurayyif_weatherapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    val apiId = "8a316bae40ca552c86771c6d73150592"
    var city = "10001"
    var wrongZip= false
    lateinit var City_TV:TextView
    lateinit var Updated_TV:TextView
    lateinit var wither_TV:TextView
    lateinit var Temperature_TV:TextView
    lateinit var LowTemperature_TV:TextView
    lateinit var HighTemperature_TV:TextView
    lateinit var WindText:TextView
    lateinit var pressureText:TextView
    lateinit var humidityText:TextView
    lateinit var SunriseText:TextView
    lateinit var SunsetText:TextView
    lateinit var refresh:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         City_TV = findViewById<TextView>(R.id.CityText)
         Updated_TV = findViewById<TextView>(R.id.UpdatedText)
         wither_TV = findViewById<TextView>(R.id.witherText)
         Temperature_TV = findViewById<TextView>(R.id.Temperature)
         LowTemperature_TV = findViewById<TextView>(R.id.LowTemperature)
         HighTemperature_TV = findViewById<TextView>(R.id.HighTemperature)
         WindText = findViewById<TextView>(R.id.WindText)
        pressureText = findViewById<TextView>(R.id.pressureText)
        humidityText = findViewById<TextView>(R.id.humidityText)
        SunriseText = findViewById<TextView>(R.id.SunriseText)
        SunsetText = findViewById<TextView>(R.id.SunsetText)
        refresh = findViewById<LinearLayout>(R.id.refresh_LL)
        request_API()
        City_TV.setOnClickListener {
            customAlert()
        }
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
            wrongZip = false
            response = URL("https://api.openweathermap.org/data/2.5/weather?zip=$city&units=metric&appid=$apiId")
                .readText(Charsets.UTF_8)
        }catch (e: Exception){
            wrongZip = true
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
            val sunrise:Long = sys.getLong("sunrise")
            val sunset:Long = sys.getLong("sunset")
            val lastUpdate:Long = jsonObj.getLong("dt")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val description = weather.getString("description")
            val main = jsonObj.getJSONObject("main")
            val temp = main.getString("temp")
            val temp_min = main.getString("temp_min")
            val temp_max = main.getString("temp_max")
            val wind = jsonObj.getJSONObject("wind")
            val speed = wind.getString("speed")
            val pressure =main.getString("pressure")
            val humidity =main.getString("humidity")
//            val tempCelsius = temp.toFloat() - 273.15
//            val temp_minCelsius = temp_min.toFloat() - 273.15
//            val temp_maxCelsius = temp_max.toFloat() - 273.15
            val tempFahrenheit = (9/5)*temp.toFloat()+32
            val temp_minFahrenheit = (9/5)*temp_min.toFloat()+32
            val temp_maxFahrenheit = (9/5)*temp_max.toFloat()+32
            var temp_Type = "Celsius"
            Temperature_TV.text ="${temp.toFloat().roundToInt()}°C"
            City_TV.text ="$name, $country"
            Updated_TV.text = "Updated at: " + SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.ENGLISH).format(Date(lastUpdate*1000))
            wither_TV.text = description[0].toUpperCase()+description.substring(1)
            LowTemperature_TV.text = "Low: ${temp_min.toFloat().roundToInt()}°C"
            HighTemperature_TV.text = "High: ${temp_max.toFloat().roundToInt()}°C"
            WindText.text = "$speed"
            pressureText.text = "$pressure"
            humidityText.text = "$humidity"
            SunriseText.text = SimpleDateFormat(
                "hh:mm a",
                Locale.ENGLISH).format(Date(sunrise*1000))
            SunsetText.text = SimpleDateFormat(
                "hh:mm a",
                Locale.ENGLISH).format(Date(sunset*1000))

            Temperature_TV.setOnClickListener {
                if(temp_Type == "Celsius"){
                    temp_Type = "Fahrenheit"
                    Temperature_TV.text ="${tempFahrenheit.roundToInt()}°F"
                    LowTemperature_TV.text = "Low: ${temp_minFahrenheit.roundToInt()}°F"
                    HighTemperature_TV.text = "High: ${temp_maxFahrenheit.roundToInt()}°F"
                }else{
                    temp_Type = "Celsius"
                    Temperature_TV.text ="${temp.toFloat().roundToInt()}°C"
                    LowTemperature_TV.text = "Low: ${temp_min.toFloat().roundToInt()}°C"
                    HighTemperature_TV.text = "High: ${temp_max.toFloat().roundToInt()}°C"
                }


            }
            refresh.setOnClickListener {
                request_API()
                Toast.makeText(applicationContext,"You are up to date",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun customAlert(){
        val dialogBuilder = AlertDialog.Builder(this)
        val input = EditText(this)
        dialogBuilder.setMessage("Entering your zip code:")
            .setPositiveButton("Submit", DialogInterface.OnClickListener {
                    dialog, id ->
                city = input.text.toString()
                request_API()
                if(wrongZip == true){
                    wrongZip = false
                    val dialogBuilder1 = AlertDialog.Builder(this)
                    dialogBuilder1.setMessage("Entering valid zip code:")
                        .setPositiveButton("OK", DialogInterface.OnClickListener {
                                dialog1, id ->
                            customAlert()
                            dialog1.cancel()
                        })

                    // create dialog box
                    val alert1 = dialogBuilder1.create()
                    alert1.show()
                    dialog.cancel()
                }else {
                    dialog.cancel()

                }
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })
        // create dialog box
        val alert = dialogBuilder.create()
        alert.setView(input)
        alert.show()
    }
}

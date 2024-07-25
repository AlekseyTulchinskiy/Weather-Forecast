package com.dippy_project.weather

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dippy_project.weather.models.WeatherModel
import com.dippy_project.weather.screens.FillBackground
import com.dippy_project.weather.screens.MainCard
import com.dippy_project.weather.screens.MainTable
import com.dippy_project.weather.screens.SearchDialog
import org.json.JSONObject

const val API_KEY = "75fd05b5f5e34950a03140749232212"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FillBackground()
            Column {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember {
                    mutableStateOf(false)
                }
                val currentDay = remember {
                    mutableStateOf(WeatherModel(
                        "",
                        "",
                        "0.0",
                        "",
                        "",
                        "0.0",
                        "0.0",
                        ""
                    ))
                }
                var currentCountry = ""
                if (dialogState.value) {
                    SearchDialog(dialogState, onSubmit = {
                        getData(it, this@MainActivity, daysList, currentDay)
                        currentCountry = it
                    })
                }
                if (currentCountry.isNotEmpty()) {
                    getData(currentCountry, this@MainActivity, daysList, currentDay)
                } else {
                    getData("Kaluga", this@MainActivity, daysList, currentDay)
                }
                MainCard(currentDay, onClickSync = {
                    getData(currentCountry, this@MainActivity, daysList, currentDay)
                }, onClickSearch = {
                    dialogState.value = true
                })
                MainTable(daysList, chosenDay = currentDay)
            }
        }
    }
}

private fun getData(city: String, context: Context, daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val url = "https://api.weatherapi.com/v1/forecast.json?" +
            "key=$API_KEY" +
            "&q=$city" +
            "&days=3" +
            "&aqi=no" +
            "&alerts=no"
    val queue = Volley.newRequestQueue(context)

    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response)
            daysList.value = list
            currentDay.value = list[0]
        },
        { error ->
            Log.d("MyLog", "Error: $error")
        }
    )
    queue.add(stringRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString() + "°C"
    )
    return list
}
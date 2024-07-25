package com.dippy_project.weather.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dippy_project.weather.models.WeatherModel
import com.dippy_project.weather.ui.theme.BlueLight

@Composable
fun MainList(list: List<WeatherModel>, chosenDay: MutableState<WeatherModel>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        itemsIndexed(
            list
        ) { _, item ->
            ListItem(item, chosenDay)
        }
    }
}

@Composable
fun ListItem(weatherModel: WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 5.dp
            )
            .clickable {
                if (weatherModel.hours.isEmpty()) return@clickable
                currentDay.value = weatherModel
            },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = BlueLight
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    )
            ) {
                Text(
                    text = weatherModel.time,
                    style = TextStyle(
                        fontSize = 18.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = weatherModel.condition,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 16.sp
                    )
                )
            }
            Text(
                text = weatherModel.currentTemp.ifEmpty {
                    "${weatherModel.maxTemp.toFloat().toInt()}°C / ${weatherModel.minTemp.toFloat().toInt()}°C"
                },
                color = Color.White,
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
            AsyncImage(
                model = "https:${weatherModel.icon}",
                contentDescription = "im2",
                modifier = Modifier
                    .size(40.dp)
                    .padding(
                        end = 8.dp
                    )
            )
        }
    }
}

@Composable
fun SearchDialog(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(onDismissRequest = {
        dialogState.value = false
    },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
            }) {
                Text(text = "Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = "cancel")
            }
        },
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Введите название города")
                TextField(value = dialogText.value, onValueChange = {
                    dialogText.value = it
                })
            }
        }
    )
}
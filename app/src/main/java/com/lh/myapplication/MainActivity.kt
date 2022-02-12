package com.lh.myapplication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.lang.System.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var prevText: List<Int>? = null
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val selectButton = findViewById<Button>(R.id.selectButton)
        selectButton.setOnClickListener(){
           on_selectButton_click()
        }
    }
    private fun on_selectButton_click(){
        val dateText = findViewById<TextView>(R.id.bottomText1)
        val hourText = findViewById<TextView>(R.id.bottomText3)

        val calendar = Calendar.getInstance()
        val selectedYear = prevText?.get(2) ?: calendar.get(Calendar.YEAR)
        val selectedMonth = prevText?.get(1)?.minus(1) ?: calendar.get(Calendar.MONTH)
        val selectedDay = prevText?.get(0) ?: calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT,
            {
                _,year,month,day ->
                    val dateFormat = SimpleDateFormat("dd/MM/yy",Locale.ENGLISH)
                    val selectedDate = dateFormat.parse("$day/${month+1}/$year")
                    val dayTilDate = (currentTimeMillis() - selectedDate.time) / 3_600_000 / 24
                    dateText.text = "$day/${month+1}/$year"
                    hourText.text = "$dayTilDate"
                    prevText = dateText.text.split("/").map { it.toInt() }

                    Toast.makeText(this,"You've lived in the world for $dayTilDate days!"
                        ,Toast.LENGTH_SHORT).show()
            },
            selectedYear,selectedMonth,selectedDay
        )
        dpd.datePicker.maxDate = currentTimeMillis() - 3_600_000 * 24
        dpd.show()
    }
}
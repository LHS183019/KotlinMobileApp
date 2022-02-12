package com.lh.calculation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.EOFException
import java.lang.Exception
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var calcBox:TextView
    private var isTypingFinished = false
    private var format = NumberFormat.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calcBox = findViewById<TextView>(R.id.calcText)
        format.isGroupingUsed = false
        format.maximumFractionDigits = 6
    }

    fun on_number_click(view: View) {
        if(!isZeroTypeing() && !isPercentExist()){
            calcBox.append((view as Button).text)
        }
        else if(isZeroTypeing() && (view as Button).text.contains(Regex("[1-9]"))){
            calcBox.text = calcBox.text.dropLast(1)
            calcBox.append((view as Button).text)
        }
        if(isOpExist()) isTypingFinished = true
    }
    fun on_op_click(view: View){
        if(!isOpExist() && isNumberTyped()){
            calcBox.append((view as Button).text)
        }
    }
    fun on_CLR_click(view: View){
        calcBox.text = ""
        isTypingFinished = false
    }
    fun on_PN_click(view: View){
        if(isNumberTyped() && !isOpExist() && calcBox.text.contains(Regex("[^0%]"))){
            if(calcBox.text.startsWith('-')){
                calcBox.text = calcBox.text.drop(1)
            } else {
                ("-" + calcBox.text).also { calcBox.text = it }
            }
        }
    }
    fun on_equal_click(view: View){
        if (isTypingFinished){
            var typing = calcBox.text
            var number1 = 1.0
            var number2 = 1.0
            var divideByZero = false
            if(typing.startsWith("-")){
                typing = typing.drop(1)
                number1 = -1.0
            }
            var typingList = typing.split(Regex("[+\\-*/]")).toMutableList()
            var op = typing[typingList[0].length]
            if (typingList[0].contains('%')){
                typingList[0] = typingList[0].dropLast(1)
                number1 /= 100
            }
            if (typingList[1].contains('%')){
                typingList[1] = typingList[1].dropLast(1)
                number2 /= 100
            }
            number1 *= typingList[0].toDouble()
            number2 *= typingList[1].toDouble()

            var result = when(op){
                '+' -> number1+number2
                '-' -> number1-number2
                '*' -> number1*number2
                '/' -> {
                    if(number2 == 0.0) {
                        Toast.makeText(this,"You can't divide by 0",Toast.LENGTH_SHORT).show()
                        divideByZero = true
                    } else {
                        number1 / number2
                    }
                }
                else -> 0
            }
            if(!divideByZero){
                calcBox.text = formatResult(format.format(result).toString())
            }
            isTypingFinished = false
        } else if(isPercentExist()) {
            var result = 1.0
            if(calcBox.text.startsWith('-')){
                result = -1.0
                calcBox.text = calcBox.text.drop(1)
            }
            result = result * calcBox.text.dropLast(1).toString().toDouble() / 100
            calcBox.text = formatResult(format.format(result)).toString()
        } else {
        }
    }
    fun on_percentage_click(view: View){
        if(isNumberTyped() && !isPercentExist()){
            calcBox.append("%")
        } else if(isPercentExist()){
            calcBox.text = calcBox.text.dropLast(1)
        }
    }
    fun on_backspace_click(view: View){
        if(calcBox.text.startsWith('-') && calcBox.text.length == 2){
            calcBox.text = calcBox.text.dropLast(2)
        } else {
            calcBox.text = calcBox.text.dropLast(1)
        }
}
    fun on_dot_click(view: View){
        if(isNumberTyped() && !isDotExist() && !isPercentExist()){
            calcBox.append(".")
        }
    }
    fun formatResult(number:String):String{
        if(number.endsWith(".0")){
            return number.substringBeforeLast('.')
        } else {
            return number
        }
    }
    fun isOpExist():Boolean{
        var temp = calcBox.text
        if(temp.startsWith('-')){
            temp = temp.drop(1)
        }
        return temp.contains(Regex("[+\\-*/]"))
    }
    fun isNumberTyped():Boolean{
        if(calcBox.text.isNotEmpty()){
            return !calcBox.text.last().toString().contains(Regex("[+\\-*/.]"))
        } else {
            return false
        }
    }
    fun isDotExist():Boolean {
        if(calcBox.text.contains('.') && !isOpExist())
        {
            return true
        } else if (isOpExist()){
            val temp = calcBox.text.split(Regex("[+\\-*/]")).last()
            if(temp.contains('.')) return true
        }
        return false
    }
    fun isPercentExist():Boolean {
        if(!isOpExist())
        {
            return calcBox.text.contains('%')
        } else if (isOpExist()){
            val temp = calcBox.text.split(Regex("[+\\-*/]")).last()
            if(temp.contains('%')) return true

        }
        return false
    }

    fun isZeroTypeing():Boolean{
        if(!isDotExist()){
            if(calcBox.text.length == 1 && calcBox.text.startsWith('0')){
                return true
            }
            if (isOpExist() && isNumberTyped()){
                val temp = calcBox.text.split(Regex("[+\\-*/]")).last()
                if(temp.startsWith('0')){
                    return true
                }
            }
        }
        return false
    }
}
package com.lh.calc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.lang.ArithmeticException
import java.lang.Exception
import java.text.NumberFormat
import kotlin.time.times

class MainActivity : AppCompatActivity() {

    private lateinit var calcBox: TextView
    private var isTypingFinished = false
    private var format = NumberFormat.getInstance()
    private var op_count = 0
    private var number_count = 0
    private var isTyping = TypingType.NONE
    private var ANS = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calcBox = findViewById<TextView>(R.id.calcText)
        format.isGroupingUsed = false
        format.maximumFractionDigits = 6
    }

    fun on_number_click(view: View) {
        if(!isZeroTypeing()){
            if(calcBox.text.endsWith(')')){
                calcBox.text = calcBox.text.dropLast(1)
                calcBox.append((view as Button).text)
                calcBox.append(")")
            } else {
                calcBox.append((view as Button).text)
            }
            isTyping = TypingType.NUMBER
            if(number_count<=op_count) number_count++
        }
        else if(isZeroTypeing() && (view as Button).text.contains(Regex("[1-9]"))){
            calcBox.text = calcBox.text.dropLast(1)
            calcBox.append((view as Button).text)
            isTyping = TypingType.NUMBER
            if(number_count<=op_count) number_count++
        }
        isTypingFinished = true
    }
    fun on_op_click(view: View){
        if(isTyping == TypingType.NUMBER){
            calcBox.append((view as Button).text)
            op_count++
            isTypingFinished = false
            isTyping = TypingType.OP
        }
    }
    fun on_CLR_click(view: View){
        calcBox.text = ""
        isTyping = TypingType.NONE
        isTypingFinished = false
    }
    fun on_PN_click(view: View){
        if(isTyping == TypingType.NUMBER && calcBox.text.contains(Regex("[^0.]"))){
            if(number_count<=1){
                if(calcBox.text.startsWith('-')){
                    calcBox.text = calcBox.text.drop(1)
                } else {
                    ("-" + calcBox.text).also { calcBox.text = it }
                }
            } else {
                val currentNumber = calcBox.text.split(Regex("[+\\-*/]")).last()
                if(currentNumber.endsWith(')')){
                    (calcBox.text.substring(0,calcBox.text.length-currentNumber.length-2)+
                            currentNumber.dropLast(1)).also { calcBox.text = it }
                } else {
                    (calcBox.text.substring(0,calcBox.text.length-currentNumber.length) +
                            "(-$currentNumber)").also { calcBox.text = it }
                }
            }
        }
    }
    fun on_equal_click(view: View){
        if (isTypingFinished) {
            var typing = calcBox.text
            var sign: MutableList<Double> = MutableList(number_count) { idex -> 1.0 }

            if (typing.startsWith("-")) {
                typing = typing.drop(1)
                sign[0] = -1.0
            }

            var inputString = ""
            var tempOpCount = 0
            for (i in typing.indices) {
                if ("+-*/".contains(typing[i]) && typing[i - 1] != '(') {
                    tempOpCount++
                    inputString += (typing[i])
                } else if (typing[i] == '-' && typing[i - 1] == '(') {
                    sign[tempOpCount] = -1.0
                } else if (typing[i] != '(' && typing[i] != ')') {
                    inputString += (typing[i])
                }
            }

            var numberArray:MutableList<String> = inputString.split(Regex("[+\\-*/]")).toMutableList()
            var opArray:String = inputString.filter { "+-*/".contains(it) }

            var numberArray2:MutableList<Double> = mutableListOf()
            var opArray2:String = inputString.filter { "+-".contains(it) }

            var i = 0
            while(i <= opArray.length){
                var tempSign = 1.0
                var enterd_while = false
                while(i < opArray.length && (opArray[i] == '*' || opArray[i] == '/')) {
                    var n1 = numberArray[i].toDouble()
                    var n2 = numberArray[i + 1].toDouble()
                    try {
                        var result = when (opArray[i]) {
                            '*' -> n1 * n2
                            '/' -> {
                                if (n2 == 0.0) throw ArithmeticException("")
                                n1 / n2
                            }
                            else -> 1
                        }
                        numberArray[i + 1] = format.format(result).toString()
                        tempSign *= sign[i] * sign[i + 1]
                        enterd_while = true
                        i++
                    } catch (ex: ArithmeticException) {
                        Toast.makeText(this, "You can't / by 0", Toast.LENGTH_SHORT).show()
                    }
                }
                if(enterd_while){
                    numberArray2.add(tempSign * numberArray[i].toDouble())
                } else{
                    numberArray2.add(sign[i] * format.format(numberArray[i].toDouble()).toDouble())
                }
                i++
            }
            i = 0
            while(i < opArray2.length) {
                var n1 = numberArray2[i]
                var n2 = numberArray2[i+1]
                var result = when(opArray2[i]) {
                    '+' -> n1+n2
                    '-' -> n1-n2
                    else -> 1
                }
                numberArray2[i+1] = result.toDouble()
                i++
            }
            var ans = if(numberArray2.isNotEmpty()) numberArray2.last() else numberArray[0].toDouble()
            calcBox.text = formatResult(format.format(ans)).toString()
            ANS = calcBox.text.toString()
            isTyping = TypingType.NUMBER
            op_count = 0
            number_count = 1
        }
    }

    fun on_Answer_click(view: View){
        if(ANS.isNotEmpty()){
            if(!isZeroTypeing()){
                if(calcBox.text.endsWith(')')){
                    calcBox.text = calcBox.text.dropLast(1)
                    calcBox.append(ANS)
                    calcBox.append(")")
                } else {
                    calcBox.append(ANS)
                }
                isTyping = TypingType.NUMBER
                if(number_count<=op_count) number_count++
            }
            else if(isZeroTypeing() && ANS.contains(Regex("[1-9]"))){
                calcBox.text = calcBox.text.dropLast(1)
                calcBox.append(ANS)
                isTyping = TypingType.NUMBER
                if(number_count<=op_count) number_count++
            }
            isTypingFinished = true
        }
    }
    fun on_backspace_click(view: View){
        if(calcBox.text.isNotEmpty()){
            if(calcBox.text.startsWith('-') && calcBox.text.length == 2){
                calcBox.text = calcBox.text.dropLast(2)
            } else if(calcBox.text.endsWith(')')){
                calcBox.text = calcBox.text.dropLast(2)
                if(calcBox.text.endsWith("(-")){
                    calcBox.text = calcBox.text.dropLast(2)
                } else {
                    calcBox.append(")")
                }
            } else if("+-*/".contains(calcBox.text.last())){
                calcBox.text = calcBox.text.dropLast(1)
                op_count--
                number_count--
            } else{
                calcBox.text = calcBox.text.dropLast(1)
            }
            if(calcBox.text.isNotEmpty()){
                if("+-*/".contains(calcBox.text.last())){
                    isTyping = TypingType.OP
                } else if (calcBox.text.last() == ')'){
                    isTyping = TypingType.NUMBER
                } else {
                    isTyping = TypingType.NUMBER
                }
            } else {
                isTyping = TypingType.NONE
            }
        }
    }

    fun on_dot_click(view: View){
        if(isTyping==TypingType.NUMBER && !isDotExist()){
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

    fun isDotExist():Boolean {
        val temp = calcBox.text.split(Regex("[+\\-*/]")).last()
            if(temp.contains('.')) return true
        return false
    }

    fun isZeroTypeing():Boolean{
        if(!isDotExist()) {
            val temp = calcBox.text.split(Regex("[+\\-*/]")).last()
            if (temp.startsWith('0')) {
                return true
            }
        }
        return false
    }
    private enum class TypingType{
        NUMBER,
        OP,
        NONE
    }
}
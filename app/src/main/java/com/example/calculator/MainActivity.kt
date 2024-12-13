package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var canAddOperation = false
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            binding.prevTV.append(view.text)
            canAddOperation = true
        }
    }

    fun operationAction(view: View){
        if (view is Button && canAddOperation){
            binding.prevTV.append(view.text)
            canAddOperation = false
        }
    }

    fun allClearActions(view: View){
        binding.prevTV.text = ""
        binding.resultTV.text = ""
    }

    fun equalsAction(view: View){
        val result = calculateResults()
        binding.resultTV.text =result
        Toast.makeText(this, "Hasil perhitungan: $result", Toast.LENGTH_SHORT).show()
    }

    private fun calculateResults(): String {
        val digitsOperators = digitOperators()
        if(digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return if (result is Int) result.toString() else result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Any {
        var result = passedList[0]

        for(i in passedList.indices) {
            if(passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1]
                result = when (operator) {
                    '+' -> if (result is Int && nextDigit is Int) result + nextDigit else (result as Float) + (nextDigit as Float)
                    '-' -> if (result is Int && nextDigit is Int) result - nextDigit else (result as Float) - (nextDigit as Float)
                    else -> result
                }
            }
        }

        return if (result is Int) result else result.toString().toFloat()
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1]
                val nextDigit = passedList[i + 1]
                when(operator) {
                    'x' -> {
                        val result = if (prevDigit is Int && nextDigit is Int) prevDigit * nextDigit else (prevDigit as Float) * (nextDigit as Float)
                        newList.add(result)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        val result = if (prevDigit is Int && nextDigit is Int && prevDigit % nextDigit == 0) prevDigit / nextDigit else (prevDigit.toString().toFloat()) / (nextDigit.toString().toFloat())
                        newList.add(result)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if(i > restartIndex)
                newList.add(passedList[i])
        }
        return newList
    }

    private fun digitOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in binding.prevTV.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }
}

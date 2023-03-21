package com.androidcollider.easyfin.common.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import com.androidcollider.easyfin.R
import com.google.android.material.color.MaterialColors

/**
 * @author Severyn Parkhomenko
 */
class CalculatorView(private val context: Context) : FrameLayout(context) {

    var separator = ","
    private val values =
        arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", separator, "0", "Backsp")
    private lateinit var tvDisplay: TextView
    var integers = ""
    var hundredths = ""
    private var workingWithIntegers = true
    var displayTextSize = 38
    var buttonsTextSize = 26
    var numberLengthLimit = 9
    var isShowSpaces = false

    fun build() {
        initKeyboard()
    }

    private fun initKeyboard() {
        val mainView =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.calculator_view,
                this
            )
        val colorOnSurface = MaterialColors.getColor(mainView, R.attr.colorOnSurface, Color.GRAY)

        tvDisplay = mainView.findViewById<View>(R.id.display) as TextView
        tvDisplay.textSize = displayTextSize.toFloat()
        tvDisplay.setTextColor(colorOnSurface)
        tvDisplay.text = "0,0"

        val selectableBackgroundOutValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            selectableBackgroundOutValue,
            true
        )

        val buttonMarginPx =
            context.resources.getDimensionPixelOffset(R.dimen.common_padding_medium)

        val keyBoardView1Row = mainView.findViewById<View>(R.id.keyboard_1row) as LinearLayout
        val keyBoardView2Row = mainView.findViewById<View>(R.id.keyboard_2row) as LinearLayout
        val keyBoardView3Row = mainView.findViewById<View>(R.id.keyboard_3row) as LinearLayout
        val keyBoardView4Row = mainView.findViewById<View>(R.id.keyboard_4row) as LinearLayout
        val rows = arrayOf(keyBoardView1Row, keyBoardView2Row, keyBoardView3Row, keyBoardView4Row)

        var rowIndex = 0
        var j = 0
        for (i in 0 until values.size - 1) {
            val btn = TextView(context)
            val params = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
            params.weight = 0.33.toFloat()
            params.setMargins(buttonMarginPx, buttonMarginPx, buttonMarginPx, buttonMarginPx)
            btn.textSize = buttonsTextSize.toFloat()
            btn.layoutParams = params
            btn.gravity = Gravity.CENTER
            btn.text = values[i]
            btn.tag = values[i]

            btn.setBackgroundResource(selectableBackgroundOutValue.resourceId)
            btn.setTextColor(colorOnSurface)

            btn.setOnClickListener(clickListener)
            rows[rowIndex].addView(btn)
            j++
            if (j == 3) {
                rowIndex++
                j = 0
            }
        }
        val fl = FrameLayout(context)
        val params = TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        params.weight = 0.33.toFloat()
        params.setMargins(buttonMarginPx, buttonMarginPx, buttonMarginPx, buttonMarginPx)
        fl.layoutParams = params

        fl.setBackgroundResource(selectableBackgroundOutValue.resourceId)

        fl.tag = values[11]
        fl.setOnClickListener(clickListener)
        keyBoardView4Row.addView(fl)
        val backsp = AppCompatImageView(context)
        val backspSizePx =
            context.resources.getDimensionPixelOffset(R.dimen.calculator_view_backspace_size)
        val backspParams = LayoutParams(backspSizePx, backspSizePx, Gravity.CENTER)
        backsp.layoutParams = backspParams
        backsp.setImageResource(R.drawable.backspace_outline)
        ImageViewCompat.setImageTintList(backsp, ColorStateList.valueOf(colorOnSurface))
        fl.addView(backsp)
    }

    private val clickListener = OnClickListener { v: View ->
        val input = v.tag as String
        performInput(input)
    }

    private fun performInput(input: String) {
        when (input) {
            separator -> {
                workingWithIntegers = false
            }
            "Backsp" -> {
                performBacksp()
            }
            else -> {
                addNumber(input)
            }
        }
    }

    private fun addNumber(inputStr: String) {
        var input = inputStr
        val inputSB: StringBuilder
        if (workingWithIntegers) {
            inputSB = StringBuilder(integers)
            if (integers.isEmpty() && input == "0" || integers.length == numberLengthLimit) {
                input = ""
            }
            inputSB.append(input)
            integers = String(inputSB)
        } else {
            inputSB = StringBuilder(hundredths)
            if (hundredths.length == 2) {
                input = ""
            }
            inputSB.append(input)
            hundredths = String(inputSB)
        }
        formatAndShow()
    }

    private fun performBacksp() {
        val inputSB: StringBuilder
        if (workingWithIntegers) {
            if (integers.isNotEmpty()) {
                inputSB = StringBuilder(integers)
                inputSB.delete(inputSB.length - 1, inputSB.length)
                integers = String(inputSB)
            }
        } else {
            inputSB = StringBuilder(hundredths)
            if (hundredths.isEmpty()) {
                workingWithIntegers = true
            } else {
                inputSB.delete(inputSB.length - 1, inputSB.length)
                hundredths = String(inputSB)
            }
        }
        formatAndShow()
    }

    fun formatAndShow() {
        val formattedIntegers = formatIntegers()
        val formattedHundredths = formatHundredths()
        var textToShow = formattedIntegers + separator + formattedHundredths
        if (isShowSpaces) {
            textToShow = addSpaces(textToShow)
        }
        tvDisplay.text = textToShow
    }

    private fun formatIntegers(): String {
        return integers.ifEmpty { "0" }
    }

    private fun formatHundredths(): String {
        if (hundredths.isEmpty()) {
            return "00"
        } else if (hundredths.length == 1) {
            return hundredths + "0"
        }
        return hundredths
    }

    private fun addSpaces(text: String): String {
        val input = text.replace("\\s+".toRegex(), "")
        var j = input.length
        if (j > 3) {
            val res: String
            if (input.contains(separator)) {
                j = input.indexOf(separator)
                val sb = StringBuilder(input.substring(0, j))
                val append = input.substring(j)
                var k = sb.length - 3
                while (k > 0) {
                    sb.insert(k, " ")
                    k -= 3
                }
                res = sb.toString() + append
            } else {
                val sb = StringBuilder(input)
                var k = sb.length - 3
                while (k > 0) {
                    sb.insert(k, " ")
                    k -= 3
                }
                res = sb.toString()
            }
            return res
        }
        return input
    }

    val calculatorValue: String
        get() = tvDisplay.text.toString()
}
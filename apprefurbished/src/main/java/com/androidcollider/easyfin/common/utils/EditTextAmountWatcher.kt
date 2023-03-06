package com.androidcollider.easyfin.common.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * @author Ihor Bilous
 */
class EditTextAmountWatcher(private val et: EditText) : TextWatcher {

    private var isInWatcher = false

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(e: Editable) {
        if (isInWatcher) return
        isInWatcher = true
        var input = et.text.toString().replace("\\s+".toRegex(), "")
        var j = input.length
        if (j > 3) {
            val res: String
            if (input.contains(",")) input = input.replace(",".toRegex(), ".")
            if (input.contains(".")) {
                j = input.indexOf(".")
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
            et.setText(res)
            et.setSelection(et.text.length)
        } else {
            et.setText(input)
            et.setSelection(et.text.length)
        }
        isInWatcher = false
    }
}
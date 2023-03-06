package com.androidcollider.easyfin.common.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.acollider.numberkeyboardview.CalculatorView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class NumericDialogFragment : DialogFragment() {

    private lateinit var frameLayout: FrameLayout
    private lateinit var tvCommit: TextView
    private lateinit var tvCancel: TextView
    private lateinit var calculatorView: CalculatorView
    private lateinit var callback: OnCommitAmountListener

    @Inject
    lateinit var numberFormatManager: NumberFormatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.frg_numeric_dialog, container, false)
        setupUI(view)
        callback = try {
            targetFragment as OnCommitAmountListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling Fragment must implement OnCommitAmountListener")
        }
        calculatorView = CalculatorView(activity)
        calculatorView.isShowSpaces = true
        calculatorView.isShowSelectors = true
        calculatorView.build()
        try {
            val inputValue = requireArguments().getString("value")
            if (inputValue != null) {
                val str = numberFormatManager.prepareStringToSeparate(inputValue)
                var integers: String
                var hundreds: String? = ""
                if (str.contains(",")) {
                    val j = str.indexOf(",")
                    integers = str.substring(0, j)
                    val h = str.substring(j + 1)
                    if (h != "00") hundreds = h
                } else integers = str
                if (integers == "0") integers = ""
                calculatorView.setIntegers(integers)
                calculatorView.setHundredths(hundreds)
                calculatorView.formatAndShow()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        frameLayout.addView(calculatorView)
        return view
    }

    private fun setupUI(view: View) {
        frameLayout = view.findViewById(R.id.containerFrgNumericDialog)
        tvCommit = view.findViewById(R.id.btnFrgNumericDialogCommit)
        tvCancel = view.findViewById(R.id.btnFrgNumericDialogCancel)
        tvCommit.setOnClickListener {
            callback.onCommitAmountSubmit(calculatorView.calculatorValue)
            dismiss()
        }
        tvCancel.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
            val height = metrics.heightPixels * 4 / 5
            val width = metrics.widthPixels * 7 / 8
            it.window?.setLayout(width, height)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    interface OnCommitAmountListener {
        fun onCommitAmountSubmit(amount: String)
    }
}
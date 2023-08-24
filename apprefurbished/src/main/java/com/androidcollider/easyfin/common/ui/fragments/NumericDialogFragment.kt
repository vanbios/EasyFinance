package com.androidcollider.easyfin.common.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.window.layout.WindowMetricsCalculator
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.ui.views.CalculatorView
import com.androidcollider.easyfin.common.utils.setSafeOnClickListener
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class NumericDialogFragment : DialogFragment() {

    private lateinit var frameLayout: FrameLayout
    private lateinit var tvCommit: TextView
    private lateinit var tvCancel: TextView
    private lateinit var calculatorView: CalculatorView

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

        activity?.let {
            calculatorView = CalculatorView(it)
            calculatorView.isShowSpaces = true
            calculatorView.build()

            val inputValue = requireArguments().getString(INPUT_VALUE)
            if (inputValue != null) {
                val str = numberFormatManager.prepareStringToSeparate(inputValue)
                var integers: String
                var hundreds = ""
                if (str.contains(",")) {
                    val j = str.indexOf(",")
                    integers = str.substring(0, j)
                    val h = str.substring(j + 1)
                    if (h != "00") hundreds = h
                } else integers = str
                if (integers == "0") integers = ""
                calculatorView.integers = integers
                calculatorView.hundredths = hundreds
                calculatorView.formatAndShow()
            }

            frameLayout.addView(calculatorView)
        }

        return view
    }

    private fun setupUI(view: View) {
        frameLayout = view.findViewById(R.id.containerFrgNumericDialog)
        tvCommit = view.findViewById(R.id.btnFrgNumericDialogCommit)
        tvCancel = view.findViewById(R.id.btnFrgNumericDialogCancel)
        tvCommit.setSafeOnClickListener {
            setFragmentResult(
                NUMERIC_DIALOG_REQUEST_KEY,
                bundleOf(OUTPUT_VALUE to calculatorView.calculatorValue)
            )
            dismiss()
        }
        tvCancel.setSafeOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dial ->
            activity?.let { act ->
                val windowMetrics =
                    WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(act)
                val currentBounds = windowMetrics.bounds
                val width = currentBounds.width() * 7 / 8
                val height = currentBounds.height() * 4 / 5
                dial.window?.setLayout(width, height)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    companion object {
        const val INPUT_VALUE = "input_value"
        const val OUTPUT_VALUE = "output_value"
        const val NUMERIC_DIALOG_REQUEST_KEY = "numeric_dialog_request_key"
    }
}
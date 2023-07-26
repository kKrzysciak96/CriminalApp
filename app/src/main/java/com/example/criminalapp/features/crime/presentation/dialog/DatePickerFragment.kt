package com.example.criminalapp.features.crime.presentation.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.*

class DatePickerFragment : DialogFragment() {
    private val args: DatePickerFragmentArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val resultDate = GregorianCalendar(year, month, dayOfMonth).time
            resultDate.hours = args.date.hours
            resultDate.minutes = args.date.minutes
            setFragmentResult(REQUEST_DATE_KEY, bundleOf(BUNDLE_DATE_KEY to resultDate))
        }

        val calendar = Calendar.getInstance()
        calendar.time = args.date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }
    companion object{
        const val REQUEST_DATE_KEY = "REQUEST_DATE_KEY"
        const val BUNDLE_DATE_KEY = "BUNDLE_DATE_KEY"
    }
}
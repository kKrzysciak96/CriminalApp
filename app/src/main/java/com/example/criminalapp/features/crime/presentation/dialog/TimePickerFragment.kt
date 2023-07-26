package com.example.criminalapp.features.crime.presentation.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.sql.Time
import java.util.*

class TimePickerFragment : DialogFragment() {
    private val args: TimePickerFragmentArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val timeMillis = args.date.time
            val newDate = Date(timeMillis)
            newDate.hours = hourOfDay
            newDate.minutes=minute
            setFragmentResult(REQUEST_TIME_KEY, bundleOf(BUNDLE_TIME_KEY to newDate))
        }
        val initialHourOfDay = args.date.hours
        val initialMinute = args.date.minutes
        val is24HourView = true

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHourOfDay,
            initialMinute,
            is24HourView
        )
    }

    companion object {
        const val REQUEST_TIME_KEY = "REQUEST_TIME_KEY"
        const val BUNDLE_TIME_KEY = "BUNDLE_TIME_KEY"
    }
}
package com.example.criminalapp.features.crime.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalapp.databinding.FragmentCriminalDetailsBinding
import com.example.criminalapp.features.crime.presentation.dialog.DatePickerFragment
import com.example.criminalapp.features.crime.presentation.dialog.TimePickerFragment
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.launch
import java.util.Date


class CriminalFragmentDetails : Fragment() {

    private var _binding: FragmentCriminalDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "Binding is not initialized" }
    private val args: CriminalFragmentDetailsArgs by navArgs()
    private val viewModel by viewModels<CrimeDetailViewModel> { CrimeDetailViewModelFactory(args.crimeId) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPress()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriminalDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.crime.collect { crime ->
                        binding.apply {
                            crime?.let { updateUI(it) }
                        }
                    }
                }
            }
        }
        setFragmentResultListener(
            requestKey = DatePickerFragment.REQUEST_DATE_KEY,
            listener ={_,bundle->
                val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_DATE_KEY) as Date
                viewModel.updateCrime { oldCrime->
                    oldCrime.copy(date =newDate )
                }
            })
        setFragmentResultListener(
            requestKey = TimePickerFragment.REQUEST_TIME_KEY,
            listener ={_,bundle->
                val newDate = bundle.getSerializable(TimePickerFragment.BUNDLE_TIME_KEY) as Date
                viewModel.updateCrime { oldCrime->
                    oldCrime.copy(date =newDate )
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUI(crime: CrimeDisplayable) {

        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeTime.text = crime.prepareDateToDisplay()
            crimeDate.setOnClickListener {
                findNavController()
                    .navigate(
                        CriminalFragmentDetailsDirections
                            .actionGoFromCriminalFragmentDetailsToDatePickerFragment(crime.date)
                    )
            }
            crimeTime.setOnClickListener {
                findNavController()
                    .navigate(
                        CriminalFragmentDetailsDirections
                            .actionGoFromCriminalFragmentDetailsToTimePickerFragment(crime.date)
                    )
            }
            crimeSolved.isChecked = crime.isSolved

        }
    }

    private fun handleBackPress() {
        if (viewModel.crime.value?.title?.isNotBlank() == true) {
            findNavController().popBackStack()
        } else {
            Toast.makeText(context, "Tytuł nie może być pusty", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun CrimeDisplayable.prepareDateToDisplay():String{
    val dateStringLeft = date.toString().substringBefore(':')
    val dateStringRight = date.toString().substringAfter(':')
    val hour = dateStringLeft.reversed().substring(0, 2).reversed()
    val minute = dateStringRight.substring(0, 2)
    return "$hour:$minute"
}
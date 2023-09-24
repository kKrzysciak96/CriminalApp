package com.example.criminalapp.features.crime.presentation.details

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalapp.R
import com.example.criminalapp.core.functions.getScaledBitmap
import com.example.criminalapp.databinding.DialogFragmentZoomedPhotoBinding
import com.example.criminalapp.databinding.FragmentCriminalDetailsBinding
import com.example.criminalapp.features.crime.presentation.dialog.DatePickerFragment
import com.example.criminalapp.features.crime.presentation.dialog.TimePickerFragment
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date


class CriminalFragmentDetails : Fragment() {

    private var _binding: FragmentCriminalDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "Binding is not initialized" }
    private val args: CriminalFragmentDetailsArgs by navArgs()
    private val viewModel by viewModels<CrimeDetailViewModel> { CrimeDetailViewModelFactory(args.crimeId) }
    private var photoName: String? = null
    private val selectSuspectApi =
        registerForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
            uri?.let {
                parseContactSelection(it)

            }
        }

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isPhotoTaken ->
            if (isPhotoTaken && photoName != null) {
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(photoFileName = photoName)
                }
            }
        }

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
        val selectSuspectIntent = selectSuspectApi.contract.createIntent(requireContext(), null)
        val takePhotoIntent = takePhoto.contract.createIntent(requireContext(), Uri.parse(""))

        binding.apply {
            chooseSuspectButton.isEnabled = canResolveIntent(selectSuspectIntent)
            chooseSuspectButton.isEnabled = canResolveIntent(takePhotoIntent)
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
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.number.collect { number ->
                        binding.apply {
                            callSuspectButton.text = number
                        }
                    }
                }
            }
        }
        setFragmentResultListener(
            requestKey = DatePickerFragment.REQUEST_DATE_KEY,
            listener = { _, bundle ->
                val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_DATE_KEY) as Date
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(date = newDate)
                }
            })
        setFragmentResultListener(
            requestKey = TimePickerFragment.REQUEST_TIME_KEY,
            listener = { _, bundle ->
                val newDate = bundle.getSerializable(TimePickerFragment.BUNDLE_TIME_KEY) as Date
                viewModel.updateCrime { oldCrime ->
                    oldCrime.copy(date = newDate)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUI(crime: CrimeDisplayable) {

        binding.apply {
            cameraPhoto.setOnClickListener {
                findNavController().navigate(
                    CriminalFragmentDetailsDirections
                        .actionGoFromCriminalFragmentDetailsToZoomedPhotoDialog(crime.photoFileName)
                )
            }
            updatePhoto(crime.photoFileName)
            takePhotoButton.setOnClickListener {
                photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.criminalapp.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }
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
            sendCrimeReportButton.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, prepareCrimeReportToSend(crime))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                }
                val chooserIntent =
                    Intent.createChooser(reportIntent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
            chooseSuspectButton.setOnClickListener {
                selectSuspectApi.launch(null)
            }
            chooseSuspectButton.text =
                crime.suspect.ifEmpty { getString(R.string.choose_suspect_button) }
            callSuspectButton.isEnabled = viewModel.number.value.isNotBlank()
            callSuspectButton.setOnClickListener {
                dialNumber(viewModel.number.value)
            }
            callSuspectButton.text =
                viewModel.number.value.ifBlank { getString(R.string.call_suspect_button) }
        }
    }

    private fun handleBackPress() {
        if (viewModel.crime.value?.title?.isNotBlank() == true) {
            findNavController().popBackStack()
        } else {
            Toast.makeText(context, "Tytuł nie może być pusty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun prepareCrimeReportToSend(crime: CrimeDisplayable): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = crime.date.toString()
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspectText)
    }

    private fun parseContactSelection(contactUri: Uri) {
        val queryFields = arrayOf(Contacts.DISPLAY_NAME, Contacts._ID)
        val queryCursor =
            requireActivity().contentResolver.query(contactUri, queryFields, null, null, null)
        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(cursor.run { getColumnIndex(Contacts.DISPLAY_NAME) })
                val id = cursor.getString(cursor.run { getColumnIndex(Contacts._ID) })
                viewModel.apply {
                    updateCrime { oldCrime ->
                        oldCrime.copy(suspect = suspect)
                    }
                }
                getPhoneNumber(id)
            }
        }
    }

    private fun getPhoneNumber(contactID: String) {
        val phoneURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val phoneNumberQueryFields = listOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val phoneWhereClause = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val phoneQueryParameters = listOf(contactID)

        val queryCursor =
            requireActivity().contentResolver.query(
                phoneURI,
                phoneNumberQueryFields.toTypedArray(),
                phoneWhereClause,
                phoneQueryParameters.toTypedArray(),
                null
            )
        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect =
                    cursor.getString(0)
                viewModel.apply {
                    updateNumber(suspect)
                }
            }
        }
    }

    fun dialNumber(number: String) {
        val dialIntent = Intent()
        dialIntent.apply {
            data = Uri.parse("tel:$number")
            action = Intent.ACTION_DIAL
        }
        startActivity(dialIntent)
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.cameraPhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.cameraPhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.cameraPhoto.setImageBitmap(scaledBitmap)
                    binding.cameraPhoto.tag = photoFileName
                }
            } else {
                binding.cameraPhoto.setImageBitmap(null)
                binding.cameraPhoto.tag = null
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
//        intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager = requireActivity().packageManager
        val resolveActivity =
            packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveActivity != null
    }
}

private fun CrimeDisplayable.prepareDateToDisplay(): String {
    val dateStringLeft = date.toString().substringBefore(':')
    val dateStringRight = date.toString().substringAfter(':')
    val hour = dateStringLeft.reversed().substring(0, 2).reversed()
    val minute = dateStringRight.substring(0, 2)
    return "$hour:$minute"
}



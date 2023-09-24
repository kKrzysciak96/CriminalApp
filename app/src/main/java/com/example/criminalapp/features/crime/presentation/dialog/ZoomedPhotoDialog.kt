package com.example.criminalapp.features.crime.presentation.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.criminalapp.R
import com.example.criminalapp.core.functions.getScaledBitmap
import com.example.criminalapp.databinding.DialogFragmentZoomedPhotoBinding
import java.io.File

class ZoomedPhotoDialog : DialogFragment(R.layout.dialog_fragment_zoomed_photo) {
    private var _binding: DialogFragmentZoomedPhotoBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "Binding is not initialized" }
    private val args: ZoomedPhotoDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentZoomedPhotoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePhoto(args.photoPath)
        Log.d("ZOOM",args.photoPath.toString())
    }
    private fun updatePhoto(photoFileName: String?) {
        if (binding.zoomedCrimePhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.zoomedCrimePhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.zoomedCrimePhoto.setImageBitmap(scaledBitmap)
                    binding.zoomedCrimePhoto.tag = photoFileName
                }
            } else {
                binding.zoomedCrimePhoto.setImageBitmap(null)
                binding.zoomedCrimePhoto.tag = null
            }
        }
    }
}
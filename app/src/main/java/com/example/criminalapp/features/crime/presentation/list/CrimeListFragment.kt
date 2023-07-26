package com.example.criminalapp.features.crime.presentation.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criminalapp.R
import com.example.criminalapp.databinding.FragmentCrimeListBinding
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.launch
import java.util.*


class CrimeListFragment : Fragment(), MenuProvider {
    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "Binding is not initialized" }
    private val viewModel by viewModels<CrimeListViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val onCrimeClick = { id: UUID ->
            findNavController().navigate(
                CrimeListFragmentDirections.actionGoFromCrimeListFragmentToCriminalFragmentDetails(
                    id
                )
            )
        }
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.crimes.collect { crimes ->
                    binding.crimeRecyclerView.adapter =
                        CrimeAdapter(crimeList = crimes, onCrimeClick = onCrimeClick)
                }
            }
        }
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.crime_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.add_new_crime) {
            showNewCrime()
            return true
        }
        return false
    }

    private fun showNewCrime() {
        val newCrime =
            CrimeDisplayable(
                id = UUID.randomUUID(),
                title = "",
                date = Date(),
                isSolved = false
            )
        viewModel.saveToLocal(newCrime)
        findNavController().navigate(
            CrimeListFragmentDirections.actionGoFromCrimeListFragmentToCriminalFragmentDetails(
                newCrime.id
            )
        )
    }
}
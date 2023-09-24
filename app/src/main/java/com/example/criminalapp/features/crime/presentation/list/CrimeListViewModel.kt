package com.example.criminalapp.features.crime.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalapp.features.crime.data.database.CrimeRepositoryImpl
import com.example.criminalapp.features.crime.data.model.CrimeRepository
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeListViewModel : ViewModel() {
    private val crimeRepository: CrimeRepository = CrimeRepositoryImpl.get()

    private val _crimes = MutableStateFlow<List<CrimeDisplayable>>(emptyList())
    val crimes: StateFlow<List<CrimeDisplayable>>
        get() = _crimes.asStateFlow()

    init {
        viewModelScope.launch {
            collectCrimes()
        }
    }

    fun saveToLocal(crime: CrimeDisplayable) {
        viewModelScope.launch { crimeRepository.saveToLocal(crime.toCrimeDomain()) }
    }

    private suspend fun collectCrimes() {
        crimeRepository.getCrimes().collect { list ->
            _crimes.value = list.map { CrimeDisplayable(it) }
        }
    }

     fun deleteCrime(crimeId: UUID) {
        viewModelScope.launch { crimeRepository.deleteCrime(crimeId) }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
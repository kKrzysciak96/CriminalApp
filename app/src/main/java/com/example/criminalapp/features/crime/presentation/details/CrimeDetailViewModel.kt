package com.example.criminalapp.features.crime.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.criminalapp.features.crime.data.database.CrimeRepositoryImpl
import com.example.criminalapp.features.crime.data.model.CrimeRepository
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class CrimeDetailViewModel(crimeId: UUID) : ViewModel() {
    private val crimeRepository: CrimeRepository = CrimeRepositoryImpl.get()
    private val _crime = MutableStateFlow<CrimeDisplayable?>(null)
    val crime = _crime.asStateFlow()

    init {
        viewModelScope.launch {
            onIdPassed(crimeId)
        }
    }

    private suspend fun onIdPassed(crimeId: UUID) {
        _crime.value = CrimeDisplayable(crimeRepository.getCrime(crimeId))
    }

    fun updateCrime(onUpdate: (CrimeDisplayable)->CrimeDisplayable){
        _crime.update { oldCrime->
            oldCrime?.let { oldCrimeNotNull -> onUpdate(oldCrimeNotNull) }
        }
    }
     private fun updateToLocal(crime:CrimeDisplayable){
        crimeRepository.updateToLocal(crime.toCrimeDomain())
    }

    override fun onCleared() {
        super.onCleared()
        crime.value?.let { updateToLocal(it) }
    }

}

class CrimeDetailViewModelFactory(private val crimeId: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrimeDetailViewModel(crimeId = crimeId) as T
    }
}
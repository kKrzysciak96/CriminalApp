package com.example.criminalapp.features.crime.data.model

import com.example.criminalapp.features.crime.domain.model.CrimeDomain
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.flow.Flow
import java.util.*

interface CrimeRepository {

    suspend fun getCrimes(): Flow<List<CrimeDomain>>

    suspend fun getCrime(crimeId: UUID): CrimeDomain

    fun updateToLocal(crime: CrimeDomain)

    suspend fun saveToLocal(crime: CrimeDomain)


}
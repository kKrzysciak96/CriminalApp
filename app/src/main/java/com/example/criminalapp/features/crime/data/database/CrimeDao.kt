package com.example.criminalapp.features.crime.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.criminalapp.features.crime.data.model.CrimeCached
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CrimeDao {

    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<CrimeCached>>

    @Query("SELECT * FROM crime WHERE id=(:crimeId)")
    suspend fun getCrime(crimeId: UUID): CrimeCached

    @Update
    suspend fun updateToLocal(crime: CrimeCached)

    @Insert
    suspend fun saveToLocal(crime: CrimeCached)

}
package com.example.criminalapp.features.crime.data.database

import android.content.Context
import androidx.room.Room
import com.example.criminalapp.features.crime.data.model.CrimeCached
import com.example.criminalapp.features.crime.data.model.CrimeRepository
import com.example.criminalapp.features.crime.domain.model.CrimeDomain
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val DATABASE_NAME = "crime-database"

class CrimeRepositoryImpl(context: Context, private val scope: CoroutineScope) : CrimeRepository {

    private val dataBase = Room.databaseBuilder(
        context,
        CrimeDataBase::class.java,
        DATABASE_NAME
    )
        .createFromAsset(DATABASE_NAME)
        .build()

    companion object {
        private var INSTANCE: CrimeRepository? = null
        fun initialize(context: Context, scope: CoroutineScope) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepositoryImpl(context, scope = scope)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository is not initialized")
        }
    }

    override suspend fun getCrimes(): Flow<List<CrimeDomain>> {
        return withContext(Dispatchers.IO) {
            dataBase.provideDao().getCrimes().map { list -> list.map { it.toCrimeDomain() } }
        }
    }

    override suspend fun getCrime(crimeId: UUID): CrimeDomain {
        return withContext(Dispatchers.IO) {
            dataBase.provideDao().getCrime(crimeId).toCrimeDomain()
        }
    }

    override fun updateToLocal(crime: CrimeDomain) {
        scope.launch { dataBase.provideDao().updateToLocal(CrimeCached(crime)) }
    }

    override suspend fun saveToLocal(crime: CrimeDomain) {
        dataBase.provideDao().saveToLocal(CrimeCached(crime))
    }

}
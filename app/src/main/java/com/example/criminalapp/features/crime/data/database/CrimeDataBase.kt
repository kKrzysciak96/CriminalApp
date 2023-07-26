package com.example.criminalapp.features.crime.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.criminalapp.features.crime.data.database.converters.DateConverter
import com.example.criminalapp.features.crime.data.model.CrimeCached

@Database(entities = [CrimeCached::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class CrimeDataBase : RoomDatabase() {
    abstract fun provideDao(): CrimeDao
}
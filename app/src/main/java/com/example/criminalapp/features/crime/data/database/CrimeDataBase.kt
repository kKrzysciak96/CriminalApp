package com.example.criminalapp.features.crime.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalapp.features.crime.data.database.converters.DateConverter
import com.example.criminalapp.features.crime.data.model.CrimeCached

@Database(entities = [CrimeCached::class], version = 3)
@TypeConverters(DateConverter::class)
abstract class CrimeDataBase : RoomDatabase() {
    abstract fun provideDao(): CrimeDao
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
    }

}

val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN photoFileName TEXT")
    }

}
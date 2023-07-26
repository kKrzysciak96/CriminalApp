package com.example.criminalapp.features.crime.data.database.converters

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun fromDate(data: Date): Long{
        return data.time
    }

    @TypeConverter
    fun toDate(data: Long): Date{
        return Date(data)
    }
}
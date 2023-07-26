package com.example.criminalapp.features.crime.presentation.model

import com.example.criminalapp.features.crime.domain.model.CrimeDomain
import java.util.*

data class CrimeDisplayable(
    val id: UUID,
    val title: String,
    val date: Date,
    val isSolved: Boolean
) {
    constructor(crime: CrimeDomain) : this(
        id = crime.id,
        title = crime.title,
        date = crime.date,
        isSolved = crime.isSolved
    )

    fun toCrimeDomain() = CrimeDomain(
        id = id,
        title = title,
        date = date,
        isSolved = isSolved
    )
}
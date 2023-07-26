package com.example.criminalapp.features.crime.domain.model

import java.util.*

data class CrimeDomain(
    val id: UUID,
    val title: String,
    val date: Date,
    val isSolved: Boolean
)

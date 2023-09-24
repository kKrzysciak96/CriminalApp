package com.example.criminalapp.features.crime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.criminalapp.features.crime.domain.model.CrimeDomain
import java.util.*

@Entity("Crime")
data class CrimeCached(
    @PrimaryKey
    val id: UUID,
    val title: String,
    val date: Date,
    val isSolved: Boolean,
    val suspect: String,
    val photoFileName: String?

) {
    constructor(crime: CrimeDomain) : this(
        id = crime.id,
        title = crime.title,
        date = crime.date,
        isSolved = crime.isSolved,
        suspect = crime.suspect,
        photoFileName = crime.photoFileName
    )

    fun toCrimeDomain() = CrimeDomain(
        id = id,
        title = title,
        date = date,
        isSolved = isSolved,
        suspect = suspect,
        photoFileName = photoFileName
    )
}

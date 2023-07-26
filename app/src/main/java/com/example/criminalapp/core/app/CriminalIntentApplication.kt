package com.example.criminalapp.core.app

import android.app.Application
import com.example.criminalapp.features.crime.data.database.CrimeRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val scope = CoroutineScope(context = SupervisorJob() + Dispatchers.IO)
        CrimeRepositoryImpl.initialize(applicationContext, scope)
    }


}
package com.silab.di

import android.content.Context
import com.silab.data.datastore.DataStore
import com.silab.data.repository.Repository

object Injection {
    fun provideRepository(context: Context): Repository {
        val dataStore = DataStore.getInstance(context)
        return Repository.getInstance(dataStore)
    }
}
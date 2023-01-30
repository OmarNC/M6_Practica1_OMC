package com.onc.m6_practica1_omc.util

import android.os.Build
import android.os.Bundle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

object Constants {
    const val BASE_URL = "https://private-a4807b-omarnc.apiary-mock.com"  //"/myweather/locations_list"
    const val LOGTAG = "LOGS"
    const val KEY_LOCALIDAD = "KEY_LOCALIDAD"

    fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T : Serializable?> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getSerializable(key, clazz)!! else (getSerializable(key) as T)
    }
}
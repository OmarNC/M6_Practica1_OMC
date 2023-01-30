package com.onc.m6_practica1_omc.network

import com.onc.m6_practica1_omc.model.Location
import com.onc.m6_practica1_omc.model.LocationDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface IMyWeatherAPI {
    @GET
    fun getLocations(
        @Url url: String?               //"https://private-a4807b-omarnc.apiary-mock.com"  //"/myweather/locations_list"
    ): Call<ArrayList<Location>>


    /*
        @GET("location_detail.php")  //La geneeración de la rita sera: location_detail.php?id=3523272
    fun getLocationDetail(
        @Url url: String?,
        @Query("id") id: Int?
    ): Call<ArrayList<LocationDetail>>

     */

    @GET("myweather/location_detail/{id}")
    fun getLocationDetail(
        @Path("id") id: String?
    ): Call<LocationDetail>

}
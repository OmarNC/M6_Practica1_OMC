package com.onc.m6_practica1_omc.model

import java.io.Serializable

data class Coord(
    //@SerializedName("lat")
    var lat: Double?,
    //@SerializedName("lon")
    var lon: Double?
): Serializable
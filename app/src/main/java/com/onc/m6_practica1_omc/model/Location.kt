package com.onc.m6_practica1_omc.model

import java.io.Serializable

data class Location(
    var id: Int?,
    var name: String?,
    var state: String?,
    var country: String?,
    var url: String?,
    var coord: Coord?
) : Serializable
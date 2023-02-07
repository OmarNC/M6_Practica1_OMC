package com.onc.m6_practica1_omc.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ManejadorPermisos (val context: Activity)
{
    companion object{
        const val PERMISO_LOCALIZACION = 1
    }

    val permissionsList = mutableListOf<String>()
    val permissionsToRequest = mutableListOf<String>()
    private var resultAll = true


    fun addPermission(permiso: String){
        var permisoTest = false
        permissionsList.add(permiso)
        permisoTest = ContextCompat.checkSelfPermission(
                        this.context,
                        permiso) == PackageManager.PERMISSION_GRANTED
        if (!permisoTest) {
            permissionsToRequest.add(permiso)
            resultAll = false
        }
    }

    fun updatePermission(){
        var permisoTest = false
        permissionsToRequest.clear()
        resultAll = true
        for (permiso in permissionsList) {
            permisoTest = ContextCompat.checkSelfPermission(
                this.context,
                permiso
            ) == PackageManager.PERMISSION_GRANTED
            if (!permisoTest) {
                permissionsToRequest.add(permiso)
                resultAll = false
            }
        }
    }

    fun requestPermission(){
        updatePermission()
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this.context,
                permissionsToRequest.toTypedArray(),
                PERMISO_LOCALIZACION
            )
        }
    }

    fun hasAllPermission(): Boolean
    {
        return resultAll
    }

}


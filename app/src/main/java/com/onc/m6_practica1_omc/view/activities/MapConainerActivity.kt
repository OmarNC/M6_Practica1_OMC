package com.onc.m6_practica1_omc.view.activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.onc.m6_practica1_omc.R
import com.onc.m6_practica1_omc.databinding.ActivityMapConainerBinding
import com.onc.m6_practica1_omc.model.Location
import com.onc.m6_practica1_omc.util.Constants
import com.onc.m6_practica1_omc.util.Constants.getSerializableCompat
import com.onc.m6_practica1_omc.util.ManejadorPermisos

class MapConainerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapConainerBinding

    var localidad: Location? = null

    //Para googleMaps
    private lateinit var map : GoogleMap

    //PAra manejar los permisos
    private  val manejadorPermisos = ManejadorPermisos(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapConainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.let { bundle ->
            if (bundle.containsKey(Constants.KEY_LOCALIDAD)) {
                localidad = bundle.getSerializableCompat(Constants.KEY_LOCALIDAD, Location::class.java)
            }
        }

        manejadorPermisos.addPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        manejadorPermisos.addPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapContainerFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    private fun updateOrRequestPermissions() {
        if (!manejadorPermisos.hasAllPermission()){
            manejadorPermisos.requestPermission()
        } else {

            //Tenemos los permisos
          //  map.isMyLocationEnabled = true

            /*


            //Actualiza la localización de mi ubicación
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000, //Cada 2 segundos actualiza la ubicación
                10F,
                this
            )

             */

        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ManejadorPermisos.PERMISO_LOCALIZACION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Se obtuvo el permiso
                    updateOrRequestPermissions()
                } else {
                    if (shouldShowRequestPermissionRationale(permissions[0])) {
                        AlertDialog.Builder(this)
                            .setTitle(getString(R.string.map_conainer_activity_aler_title))
                            .setMessage(getString(R.string.map_conainer_activity_aler_message))
                            .setPositiveButton(
                                getString(R.string.map_conainer_activity_aler_positive_button),
                                DialogInterface.OnClickListener { _, _ ->
                                    updateOrRequestPermissions()
                                })
                            .setNegativeButton(
                                getString(R.string.map_conainer_activity_alernegative_button),
                                DialogInterface.OnClickListener { dialog, _ ->
                                    dialog.dismiss()
                                    finish()
                                })
                            .create()
                            .show()
                    } else {
                        //Si el usuario no quiere que nunca se le vuelva a preguntar por el permiso
                        Toast.makeText(
                            this,
                            getString(R.string.map_conainer_activity_toast_denegate_message),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMapa: GoogleMap) {
        map = googleMapa
        createMarker()
        manejadorPermisos.requestPermission()
    }

    fun createMarker(){
        if ((localidad != null) && (localidad?.coord != null)) {
            val coordinates = LatLng(localidad?.coord?.lat!!, localidad?.coord?.lon!!)
            val marker = MarkerOptions()
                .position(coordinates)
                .title(localidad?.name)
                .snippet("${localidad?.state}, ${localidad?.country}")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_pin_icon))

            map.addMarker(marker)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 14f),
                4000,
                null
            )
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!::map.isInitialized) return
        manejadorPermisos.requestPermission()
    }
}
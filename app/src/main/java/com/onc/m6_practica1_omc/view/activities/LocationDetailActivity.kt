package com.onc.m6_practica1_omc.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.onc.m6_practica1_omc.R
import com.onc.m6_practica1_omc.databinding.ActivityLocationDetailBinding
import com.onc.m6_practica1_omc.network.IMyWeatherAPI
import com.onc.m6_practica1_omc.model.Location
import com.onc.m6_practica1_omc.model.LocationDetail
import com.onc.m6_practica1_omc.util.Constants
import com.onc.m6_practica1_omc.util.Constants.getSerializableCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LocationDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLocationDetailBinding


    private lateinit var localidad : Location
    private  var localidadDetalle : LocationDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData()
    {
        intent.extras?.let { bundle ->
            if (bundle.containsKey(Constants.KEY_LOCALIDAD)) {
                localidad = bundle.getSerializableCompat(Constants.KEY_LOCALIDAD, Location::class.java)

                CoroutineScope(Dispatchers.IO).launch {

                    val call = Constants.getRetrofit().create(IMyWeatherAPI::class.java)
                        .getLocationDetail(localidad.id.toString())

                    call.enqueue(object : Callback<LocationDetail> {
                        override fun onResponse(
                            call: Call<LocationDetail>,
                            response: Response<LocationDetail>
                        ) {
                            Log.d(Constants.LOGTAG, "La respuesta del servicio: ${response.toString()}")
                            Log.d(Constants.LOGTAG, "Los datos recibidos: ${response.body()}")
                            localidadDetalle = response.body()

                            fillViewControls(localidadDetalle)

                        }

                        override fun onFailure(call: Call<LocationDetail>, t: Throwable) {
                            Log.d(
                                Constants.LOGTAG,
                                "No se pudo establecer conexión al sitio: ${Constants.BASE_URL} \n " +
                            "ERROR: ${t.message}"
                            )
                            Toast.makeText(
                                this@LocationDetailActivity,
                                "${getString(R.string.local_detail_activity_fail_conection)} ${Constants.BASE_URL} \n " +
                                        "${getString(R.string.error)} ${t.message}",
                                Toast.LENGTH_LONG).show()

                            binding.pbConexion.visibility = View.GONE
                        }

                    })
                }

            }else { //Se inició la actividad sin el parámetro de la localidad
                Toast.makeText(this@LocationDetailActivity, getString(R.string.error_localidad), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun fillViewControls(localidadDetalle : LocationDetail?)
    {
            binding.pbConexion.visibility = View.GONE
            if (localidadDetalle != null) {
                binding.tvCiudadName.text = localidad.name
                binding.tvFechaHora.text =
                    dateTimeToString(localidadDetalle.dt?.toLong() ?: 0, "h:mm a")
                binding.tvTemperatura.text = "${localidadDetalle.main?.temp}°C"
                binding.tvTempSensation.text = "${getString(R.string.location_detail_sensacion)} ${localidadDetalle.main?.feels_like}°C"
                binding.tvDescripcion.text = localidadDetalle.weather?.get(0)?.description ?: ""
                binding.tvPresionValor.text = "${localidadDetalle.main?.pressure} hPa"
                binding.tvHumedadValor.text = "${localidadDetalle.main?.humidity} %"
                binding.tvTempMinValor.text = "${localidadDetalle.main?.temp_min}°C"
                binding.tvTempMaxValor.text = "${localidadDetalle.main?.temp_max}°C"
                binding.tvVelVientoValor.text = "${localidadDetalle.wind?.speed} m/s"
                binding.tvDirVientoValor.text = "${localidadDetalle.wind?.deg}°"
                binding.tvVisibilidadValor.text = "${localidadDetalle.visibility?.div(1000.0)} km"
                binding.tvSalidaSolValor.text = dateTimeToString(
                    localidadDetalle.sys?.sunrise?.toLong() ?: 0, "h:mm a"
                )
                binding.tvPuestaSolValor.text = dateTimeToString(
                    localidadDetalle.sys?.sunset?.toLong() ?: 0, "h:mm a"
                )
                binding.tvLatitudValor.text = "${localidadDetalle.coord?.lat}"
                binding.tvLongitudValor.text = "${localidadDetalle.coord?.lon}"

                var url ="http://openweathermap.org/img/wn/" + localidadDetalle.weather?.get(0)?.icon + "@2x.png"
                Glide.with(this@LocationDetailActivity)
                    .load(url)
                    .placeholder(R.drawable.circulo_naranja)
                    .into(binding.ivIcon)

        }

    }

    private fun dateTimeToString(miliseconds: Long, format: String) : String
    {
        val date = Date(miliseconds)
        //val simpleDateFormat  = SimpleDateFormat("dd/mm/yyyy h:mm a")
        val simpleDateFormat  = SimpleDateFormat(format, Locale.getDefault())
        return simpleDateFormat.format(date)
    }


}
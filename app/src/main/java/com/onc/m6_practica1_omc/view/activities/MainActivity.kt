package com.onc.m6_practica1_omc.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.onc.m6_practica1_omc.R
import com.onc.m6_practica1_omc.databinding.ActivityMainBinding
import com.onc.m6_practica1_omc.network.IMyWeatherAPI
import com.onc.m6_practica1_omc.model.Location
import com.onc.m6_practica1_omc.util.Constants
import com.onc.m6_practica1_omc.view.adapters.LocationsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), IRecycleViewItemLocationSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAnimation()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun setAnimation(){
        val miAnimation = AnimationUtils.loadAnimation(this, R.anim.main_cloud_animation)
        binding.ivCloud.startAnimation(miAnimation)
    }


    private fun loadData()
    {
        CoroutineScope(Dispatchers.IO).launch {

            val call = Constants.getRetrofit().create(IMyWeatherAPI::class.java)
                .getLocations("myweather/locations_list")

            call.enqueue(object : Callback<ArrayList<Location>> {
                override fun onResponse(
                    call: Call<ArrayList<Location>>,
                    response: Response<ArrayList<Location>>
                ) {
                    Log.d(Constants.LOGTAG, "La respuesta del servicio: ${response.toString()}")
                    Log.d(Constants.LOGTAG, "La respuesta del servicio: ${response.body()}")

                    binding.pbConexion.visibility= View.GONE
                    binding.rvLocations.layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.rvLocations.adapter = LocationsAdapter(this@MainActivity, response.body()!!, this@MainActivity)
                }

                override fun onFailure(call: Call<ArrayList<Location>>, t: Throwable) {
                    Log.d(
                        Constants.LOGTAG,
                        "No se pudo establecer conexión al sitio: ${Constants.BASE_URL}"
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "${getString(R.string.local_detail_activity_fail_conection)}: ${Constants.BASE_URL} \n " +
                                "${getString(R.string.error)} ${t.message}",
                        Toast.LENGTH_LONG).show()

                    binding.pbConexion.visibility = View.GONE
                }

            })
        }
    }

    override fun OnItemSelected(location: Location) {

        val intent = Intent(this@MainActivity, LocationDetailActivity::class.java).apply {
            //Enviando un objeto a la actividad
            putExtra(Constants.KEY_LOCALIDAD, location)
        }
        startActivity(intent)
    }
}
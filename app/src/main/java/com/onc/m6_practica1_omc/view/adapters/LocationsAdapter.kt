package com.onc.m6_practica1_omc.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onc.m6_practica1_omc.R
import com.onc.m6_practica1_omc.databinding.LocationItemBinding
import com.onc.m6_practica1_omc.model.Location
import com.onc.m6_practica1_omc.util.Constants
import com.onc.m6_practica1_omc.view.activities.IRecycleViewItemLocationSelectedListener

class LocationsAdapter(
    private var context: Context,
    private val locations: ArrayList<Location>,
    private val listener: IRecycleViewItemLocationSelectedListener):
RecyclerView.Adapter<LocationsAdapter.ViewHolder>(){

    class ViewHolder(view: LocationItemBinding) : RecyclerView.ViewHolder(view.root) {
        val ivImagen = view.ivImagen
        var tvName = view.tvName
        var tvState = view.tvState
        var tvSountry = view.tvCountry
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LocationItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return  locations.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = locations[position].name
        holder.tvState.text = locations[position].state
        holder.tvSountry.text = locations[position].country

        Glide.with(context)
            .load(locations[position].url)
            .placeholder(R.drawable.location_city)
            .into(holder.ivImagen)

        holder.itemView.setOnClickListener{
            Log.d(Constants.LOGTAG, "Se enviará el objeto: ${locations[position]}")
            listener.OnItemSelected(locations[position])
        }
    }
}
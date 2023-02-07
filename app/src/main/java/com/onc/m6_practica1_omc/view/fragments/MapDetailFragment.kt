package com.onc.m6_practica1_omc.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.onc.m6_practica1_omc.R
import com.onc.m6_practica1_omc.databinding.FragmentMapDetailBinding
import com.onc.m6_practica1_omc.model.Location
import com.onc.m6_practica1_omc.util.Constants
import com.onc.m6_practica1_omc.util.Constants.getSerializableCompat

class MapDetailFragment : Fragment() {

    private var _binding : FragmentMapDetailBinding? = null
    private val binding get() = _binding!!

    private var localidad: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        if (arguments != null && arguments?.containsKey(Constants.KEY_LOCALIDAD)!!) {
            localidad = requireArguments().getSerializableCompat(Constants.KEY_LOCALIDAD, Location::class.java)
        }
            return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(localidad: Location) =
            MapDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(Constants.KEY_LOCALIDAD, localidad)
                }
            }
    }
}
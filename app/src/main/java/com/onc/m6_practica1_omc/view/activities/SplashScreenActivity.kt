package com.onc.m6_practica1_omc.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.onc.m6_practica1_omc.R
import com.onc.m6_practica1_omc.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val miAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_title_animation)
        binding.tvTitle.startAnimation(miAnimation)

        val r = getResources()
        val duration = r.getInteger(R.integer.splash_duration)+100
        //Toast.makeText(this, "Duration : ${duration.toLong()}", Toast.LENGTH_LONG).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }, duration.toLong())
    }

}
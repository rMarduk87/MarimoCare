package rpt.tool.marimocare

import android.R.attr.duration
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.animation.addListener
import rpt.tool.marimocare.databinding.ActivitySplashScreenBinding
import kotlin.math.pow
import kotlin.random.Random


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var animation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.splashImageView.setBackgroundResource(R.drawable.splash_animation)
        animation = binding.splashImageView.background as AnimationDrawable


        // Avvia animazione dopo il layout completo
        binding.splashImageView.post {
            animation.start()

            // Calcolo durata totale
            val totalDuration = (animation.numberOfFrames * 80).toLong()

            // Al termine dell'animazione, apri MainActivity
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, totalDuration)
        }

    }

}
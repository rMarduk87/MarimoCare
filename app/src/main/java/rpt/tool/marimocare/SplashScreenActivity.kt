package rpt.tool.marimocare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import rpt.tool.marimocare.databinding.ActivitySplashScreenBinding


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    var handler: Handler? = null
    var runnable: Runnable? = null
    var millisecond: Int = 1000



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        Handler(Looper.getMainLooper()).postDelayed({

            binding.titleText.visibility = View.VISIBLE

            binding.titleText.alpha = 0f

            binding.titleText.animate().alpha(1f).setDuration(250).start()


        }, 890)

        Handler(Looper.getMainLooper()).postDelayed({

            binding.subtitleText.visibility = View.VISIBLE

            binding.subtitleText.alpha = 0f

            binding.subtitleText.animate().alpha(1f).setDuration(250).start()

        }, 940)
    }

    @SuppressLint("UnsafeIntentLaunch")
    override fun onResume() {
        super.onResume()


        runnable = Runnable {
            val  intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        handler = Handler(Looper.getMainLooper())
        handler!!.postDelayed(runnable!!, millisecond.toLong())
    }

}
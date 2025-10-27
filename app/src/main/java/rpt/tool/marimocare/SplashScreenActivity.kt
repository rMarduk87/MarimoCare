package rpt.tool.marimocare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
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
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @SuppressLint("UnsafeIntentLaunch")
    override fun onResume() {
        super.onResume()


        runnable = Runnable {
            val  intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        handler = Handler()
        handler!!.postDelayed(runnable!!, millisecond.toLong())
    }

}
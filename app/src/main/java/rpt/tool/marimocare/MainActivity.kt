package rpt.tool.marimocare

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import rpt.tool.marimocare.databinding.ActivityMainBinding
import rpt.com.base.BaseActivity
import rpt.tool.marimocare.ui.dashboard.DashboardFragmentDirections

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()
        handleIntent(intent)
    }


    fun Activity.hideSystemBars() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.hide(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val data = intent.data ?: return

        if (data.scheme == "rpt"
            && data.host == "tool.marimocare"
            && data.path == "/open") {

            val code = data.getQueryParameter("code") ?: return
            val name = data.getQueryParameter("name") ?: ""

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.main_activity_nav_host_fragment) as
                        NavHostFragment
            val navController = navHostFragment.navController

            val currentDestinationId = navController.currentDestination?.id

            // Evita navigazioni doppie / crash
            if (currentDestinationId != R.id.fromQRCodeMarimoFragment) {

                val action =
                    DashboardFragmentDirections
                        .actionDashboardFragmentToFromQRCodeMarimoFragment(code, name)

                try {
                    navController.navigate(action)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_activity_nav_host_fragment) as
                    NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
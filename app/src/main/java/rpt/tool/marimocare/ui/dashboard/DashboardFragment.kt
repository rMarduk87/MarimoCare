package rpt.tool.marimocare.ui.dashboard

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import rpt.tool.marimocare.utils.view.recyclerview.items.client.hooks.ChangeWaterEventHook
import rpt.tool.marimocare.utils.view.recyclerview.items.client.hooks.EditMarimoEventHook
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.databinding.FragmentDashboardBinding
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.view.defaultSetUp
import rpt.tool.marimocare.utils.view.gone
import rpt.tool.marimocare.utils.view.recyclerview.items.client.MarimoItem
import rpt.tool.marimocare.utils.view.visible
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.getValue

class DashboardFragment: BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate) {

    private val itemAdapter = ItemAdapter<MarimoItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    private val viewModel: DashboardViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerMarimos.defaultSetUp(
            fastAdapter,
            ChangeWaterEventHook(),
            EditMarimoEventHook()
        )

        viewModel.marimoItems.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.recyclerMarimos.gone()
                binding.emptyListLabel!!.visible()
            } else {
                binding.recyclerMarimos.visible()
                binding.emptyListLabel!!.gone()
                itemAdapter.set(it)
            }
        }

        binding.btnAddMarimo.setOnClickListener {
            // Apri schermata di aggiunta (es. AddMarimoActivity)
        }

        binding.btnAddMarimoHeader.setOnClickListener {
            // Apri schermata di aggiunta (es. AddMarimoActivity)
        }

        binding.btnDashboardHeader.setOnClickListener {
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun daysUntil(date: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val nextChange = LocalDate.parse(date, formatter)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, nextChange).toInt()
        } catch (e: Exception) {
            0
        }
    }
}
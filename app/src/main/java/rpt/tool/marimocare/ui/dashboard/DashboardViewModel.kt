package rpt.tool.marimocare.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem

class DashboardViewModel : ViewModel() {

    private val marimos = mutableListOf<Marimo>()


    val marimoItems = liveData {
        withContext(Dispatchers.Main) {
            emitSource(RepositoryManager.marimoRepository.marimos.map {
                it.map { MarimoItem(it) }
            })
        }
    }
}
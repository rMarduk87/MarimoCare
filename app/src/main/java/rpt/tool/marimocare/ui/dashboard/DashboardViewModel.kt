package rpt.tool.marimocare.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import rpt.tool.marimocare.utils.log.d
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem

class DashboardViewModel : ViewModel() {

    val allMarimos: LiveData<List<Marimo>> = RepositoryManager.marimoRepository.marimos

    val marimoItems: LiveData<List<MarimoItem>> = allMarimos.map { marimos ->
        d("ViewModelLog", "All Marimos LiveData updated. Count: ${marimos.size}")

        marimos.map { marimo ->
            MarimoItem(marimo)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    val overdueMarimo = getOverdueMarimoCounter(marimoItems)

    @RequiresApi(Build.VERSION_CODES.O)
    val dueSoonMarimo =  getDueSoonMarimoCounter(marimoItems)

    @RequiresApi(Build.VERSION_CODES.O)
    val upToDateMarimo =  getUpToDateMarimoCounter(marimoItems)



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getOverdueMarimoCounter(marimoItems: LiveData<List<MarimoItem>>): LiveData<Int> =
        marimoItems.map { items ->
            items.count { MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.OVERDUE }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDueSoonMarimoCounter(marimoItems: LiveData<List<MarimoItem>>): LiveData<Int> =
        marimoItems.map { items ->
            items.count { MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.DUE_SOON }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUpToDateMarimoCounter(marimoItems: LiveData<List<MarimoItem>>): LiveData<Int> =
        marimoItems.map { items ->
            items.count { MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.NORMAL }
        }


}
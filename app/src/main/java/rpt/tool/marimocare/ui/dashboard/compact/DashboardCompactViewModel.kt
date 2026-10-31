package rpt.tool.marimocare.ui.dashboard.compact

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import rpt.com.base.log.d
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.compact.MarimoCompactItem

class DashboardCompactViewModel : ViewModel() {

    val allMarimos: LiveData<List<Marimo>> = RepositoryManager.marimoRepository.marimos

    val marimoItems: LiveData<List<MarimoCompactItem>> = allMarimos.map<List<Marimo>, List<MarimoCompactItem>> { marimos ->
        d("ViewModelLog", "All Marimos LiveData updated. Count: ${marimos.size}")

        marimos.map { marimo ->
            MarimoCompactItem(marimo)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    val overdueMarimo = getOverdueMarimoCounter(marimoItems)

    @RequiresApi(Build.VERSION_CODES.O)
    val dueSoonMarimo =  getDueSoonMarimoCounter(marimoItems)

    @RequiresApi(Build.VERSION_CODES.O)
    val upToDateMarimo =  getUpToDateMarimoCounter(marimoItems)

    @RequiresApi(Build.VERSION_CODES.O)
    val allMarimosToUpdate = getMarimosToUpdate(allMarimos)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMarimosToUpdate(allMarimos: LiveData<List<Marimo>>): LiveData<List<Marimo>> =
        allMarimos.map<List<Marimo>, List<Marimo>> { marimos -> marimos.filter {
            MarimoStatus.from(it.daysLeft) == MarimoStatus.OVERDUE } }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getOverdueMarimoCounter(marimoItems: LiveData<List<MarimoCompactItem>>): LiveData<Int> =
        marimoItems.map<List<MarimoCompactItem>, Int> { items ->
            items.count { MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.OVERDUE }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDueSoonMarimoCounter(marimoItems: LiveData<List<MarimoCompactItem>>): LiveData<Int> =
        marimoItems.map<List<MarimoCompactItem>, Int> { items ->
            items.count { MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.DUE_SOON }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUpToDateMarimoCounter(marimoItems: LiveData<List<MarimoCompactItem>>): LiveData<Int> =
        marimoItems.map<List<MarimoCompactItem>, Int> { items ->
            items.count { MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.NORMAL }
        }


}

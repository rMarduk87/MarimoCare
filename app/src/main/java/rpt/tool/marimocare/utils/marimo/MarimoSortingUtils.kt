package rpt.tool.marimocare.utils.marimo

import android.os.Build
import androidx.annotation.RequiresApi
import com.mikepenz.fastadapter.adapters.ItemAdapter
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem

object MarimoSortingUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sortBy(
        originalList: List<MarimoItem>,
        itemAdapter: ItemAdapter<MarimoItem>,
        position: Int
    ) {
        when (position) {
            0->{
                val sorted = originalList.sortedBy { MarimoStatus.from(it.marimo.daysLeft) }
                itemAdapter.set(sorted)
            }
            1->{
                val sorted = originalList.sortedBy { it.marimo.name }
                itemAdapter.set(sorted)
            }
            2->{
                val sorted = originalList.sortedBy { it.marimo.lastChanged }
                itemAdapter.set(sorted)
            }
        }
    }
}
package rpt.tool.marimocare.utils.marimo

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem
import com.mikepenz.fastadapter.adapters.ItemAdapter

object MarimoFilterUtils {


    @RequiresApi(Build.VERSION_CODES.O)
    fun filterByStatus(
        originalList: List<MarimoItem>,
        itemAdapter: ItemAdapter<MarimoItem>,
        status: MarimoStatus?
    ) {

        if (status == null) {
            itemAdapter.set(originalList)
            return
        }

        val filtered = originalList.filter { MarimoStatus.from(it.marimo.daysLeft) == status }

        itemAdapter.set(filtered)
    }
}
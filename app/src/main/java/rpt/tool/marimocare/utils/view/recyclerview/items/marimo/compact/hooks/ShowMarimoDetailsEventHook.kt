package rpt.tool.marimocare.utils.view.recyclerview.items.marimo.compact.hooks

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.ui.dashboard.classic.DashboardFragmentDirections
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.getFastAdapterItemViewBinding
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.compact.MarimoCompactItem

class ShowMarimoDetailsEventHook : ClickEventHook<MarimoCompactItem>() {

    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        val binding = viewHolder.getFastAdapterItemViewBinding<ItemMarimoBinding>()
        return binding?.btnDetails
    }

    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<MarimoCompactItem>,
        item: MarimoCompactItem
    ) {
        v.safeNavController()?.safeNavigate(
            DashboardFragmentDirections
                .actionDashboardFragmentToMarimoDetailFragment(item.marimo.code)
        )
    }
}
package rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.ui.dashboard.DashboardFragmentDirections
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.getFastAdapterItemViewBinding
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem

class EditMarimoEventHook : ClickEventHook<MarimoItem>() {


    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        val binding = viewHolder.getFastAdapterItemViewBinding<ItemMarimoBinding>()
        return binding?.btnEdit
    }

    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<MarimoItem>,
        item: MarimoItem
    ) {
        v.safeNavController?.safeNavigate(
            DashboardFragmentDirections
                .actionDashboardFragmentToAddOrEditFragment(item.marimo.code)
        )
    }
}
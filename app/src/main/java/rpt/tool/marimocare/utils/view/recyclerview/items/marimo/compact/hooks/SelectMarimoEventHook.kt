package rpt.tool.marimocare.utils.view.recyclerview.items.marimo.compact.hooks

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.compact.MarimoCompactItem

class SelectMarimoEventHook(
    private val isSelectionModeActive: () -> Boolean,
    private val onToggleSelection: (MarimoCompactItem) -> Unit
) : ClickEventHook<MarimoCompactItem>() {

    override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
        val view = viewHolder.itemView

        val card = view.findViewById<View>(R.id.cardMarimo)
        val checkbox = view.findViewById<View>(R.id.checkboxSelection)

        return listOfNotNull(card, checkbox)
    }

    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<MarimoCompactItem>,
        item: MarimoCompactItem
    ) {
        if (isSelectionModeActive()) {
            onToggleSelection(item)
        }
    }
}
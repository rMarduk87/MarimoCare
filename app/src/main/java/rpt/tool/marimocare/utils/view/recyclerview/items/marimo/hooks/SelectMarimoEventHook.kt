package rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem

class SelectMarimoEventHook(
    private val isSelectionModeActive: () -> Boolean,
    private val onToggleSelection: (MarimoItem) -> Unit
) : ClickEventHook<MarimoItem>() {

    override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
        val view = viewHolder.itemView

        val card = view.findViewById<View>(R.id.cardMarimo)
        val checkbox = view.findViewById<View>(R.id.checkboxSelection)

        return listOfNotNull(card, checkbox)
    }

    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<MarimoItem>,
        item: MarimoItem
    ) {
        if (isSelectionModeActive()) {
            onToggleSelection(item)
        }
    }
}
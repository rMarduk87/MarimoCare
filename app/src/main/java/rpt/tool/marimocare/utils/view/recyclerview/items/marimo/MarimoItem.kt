package rpt.tool.marimocare.utils.view.recyclerview.items.marimo

import android.annotation.SuppressLint
import androidx.core.graphics.toColorInt
import rpt.tool.marimocare.utils.view.recyclerview.BaseRecyclerViewBindingItem

import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.utils.data.appmodels.Marimo


class MarimoItem(val marimo: Marimo) :
    BaseRecyclerViewBindingItem<ItemMarimoBinding>(ItemMarimoBinding::inflate) {

    override val type: Int = R.id.rv_marimo

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: ItemMarimoBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)

        binding.txtName.text = marimo.name
        binding.txtFrequency.text =
            binding.root.resources.getString(R.string.changes_every_days, marimo.changeFrequencyDays)
        binding.txtLastChange.text = marimo.lastChanged
        binding.txtNextChange.text = marimo.nextChange
        binding.txtNotes.text = marimo.notes ?: binding.root.resources.getString(R.string.no_notes)

        val daysLeft = marimo.daysLeft
        binding.txtDaysLeft.text = daysLeft.toString() + " " +
                binding.root.resources.getString(R.string.days_left)

        /*val bgColor = if (daysLeft < 0) "#FFE5E5" else "#E3F9F1"
        binding.txtDaysLeft.setBackgroundColor(bgColor.toColorInt())*/
    }

    override fun unbindView(binding: ItemMarimoBinding) {

    }
}
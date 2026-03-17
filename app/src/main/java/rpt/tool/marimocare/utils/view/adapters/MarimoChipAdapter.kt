package rpt.tool.marimocare.utils.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.appmodels.Marimo

class MarimoChipAdapter(
    private val onSelectionChanged: (List<Marimo>) -> Unit
) : RecyclerView.Adapter<MarimoChipAdapter.ChipViewHolder>() {

    private var marimos: List<Marimo> = emptyList()

    private val selectedMarimoCodes = mutableSetOf<Int>()

    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMarimoName: TextView = itemView.findViewById(R.id.tvMarimoName)

        fun bind(marimo: Marimo) {
            tvMarimoName.text = marimo.name

            itemView.isSelected = selectedMarimoCodes.contains(marimo.code)

            itemView.setOnClickListener {
                if (selectedMarimoCodes.contains(marimo.code)) {
                    selectedMarimoCodes.remove(marimo.code)
                } else {
                    selectedMarimoCodes.add(marimo.code)
                }

                notifyItemChanged(adapterPosition)

                val selectedMarimosList = marimos.filter { selectedMarimoCodes.contains(it.code) }
                onSelectionChanged(selectedMarimosList)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marimo_chip, parent, false)
        return ChipViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(marimos[position])
    }

    override fun getItemCount(): Int = marimos.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Marimo>) {
        marimos = newList
        notifyDataSetChanged()
    }
}
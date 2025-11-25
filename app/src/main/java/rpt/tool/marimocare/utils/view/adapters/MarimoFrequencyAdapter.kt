package rpt.tool.marimocare.utils.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.view.recyclerview.items.frequency.MarimoFrequencyItem

class MarimoFrequencyAdapter(
    private val items: List<MarimoFrequencyItem>
) : RecyclerView.Adapter<MarimoFrequencyAdapter.MarimoViewHolder>() {

    inner class MarimoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card = view.findViewById<MaterialCardView>(R.id.cardContainer)
        val name = view.findViewById<TextView>(R.id.txtName)
        val freq = view.findViewById<TextView>(R.id.txtFreq)
        val last = view.findViewById<TextView>(R.id.txtLastChanged)
        val notes = view.findViewById<TextView>(R.id.txtNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarimoViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marimo_frequency, parent, false)
        return MarimoViewHolder(v)
    }

    override fun onBindViewHolder(holder: MarimoViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.name
        holder.freq.text = item.frequencyDays
        holder.last.text = item.lastChanged
        holder.notes.text = item.notes

        holder.freq.setTextColor(item.frequencyColor)
        holder.last.setTextColor(item.lastChangedColor)
        holder.card.setCardBackgroundColor(item.cardBackgroundColor)
    }

    override fun getItemCount(): Int = items.size
}
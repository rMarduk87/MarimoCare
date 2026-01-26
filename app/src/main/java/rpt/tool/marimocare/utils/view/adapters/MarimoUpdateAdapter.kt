package rpt.tool.marimocare.utils.view.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.data.appmodels.MarimoUpdate

class MarimoUpdateAdapter(
    private val items: List<MarimoUpdate>,
    private val onSelectionChanged: () -> Unit,
) : RecyclerView.Adapter<MarimoUpdateAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view as MaterialCardView
        val check: CheckBox = view.findViewById(R.id.checkMarimo)
        val name: TextView = view.findViewById(R.id.txtName)
        val overdue: TextView = view.findViewById(R.id.txtOverdue)
        val last: TextView = view.findViewById(R.id.txtLastChange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_update_marimo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val marimo = items[position]

        holder.name.text = marimo.name
        holder.overdue.text = buildString {
            append(marimo.name)
            append(" ")
            append(holder.itemView.context.getString(R.string.is_adapter))
            append(" ")
            append(marimo.overdueDays)
            append(" ")
            append(holder.itemView.context.getString(R.string.days_overdue_adapter))
        }
        holder.last.text = holder.itemView.context
            .getString(R.string.last_changed_adapter) +" "+ marimo.lastChanged

        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = marimo.selected

        updateCardStyle(holder.card, marimo.selected)

        holder.check.setOnCheckedChangeListener { _, isChecked ->
            marimo.selected = isChecked
            updateCardStyle(holder.card, isChecked)
            onSelectionChanged()
        }

        holder.itemView.setOnClickListener {
            holder.check.isChecked = !holder.check.isChecked
        }
    }

    override fun getItemCount() = items.size

    private fun updateCardStyle(card: MaterialCardView, selected: Boolean) {
        if (selected) {
            card.setCardBackgroundColor(
                ContextCompat.getColor(card.context, R.color.marimo_item_edit_board)
            )
            card.strokeColor =
                ContextCompat.getColor(card.context, R.color.green)
        } else {
            card.setCardBackgroundColor(Color.WHITE)
            card.strokeColor = Color.WHITE
        }
    }
}
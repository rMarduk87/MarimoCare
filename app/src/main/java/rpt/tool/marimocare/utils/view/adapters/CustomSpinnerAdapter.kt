package rpt.tool.marimocare.utils.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import rpt.tool.marimocare.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>,
    private var selectedIndex: Int = -1
) : ArrayAdapter<String>(context, 0, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Spinner chiuso → usa layout senza spunta
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner_selected, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinnerText)
        textView.text = items[position]
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Dropdown → layout con spunta
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner_dropdown, parent, false)

        val textView = view.findViewById<TextView>(R.id.spinnerText)
        val checkIcon = view.findViewById<ImageView>(R.id.checkIcon)

        textView.text = items[position]
        checkIcon.visibility = if (position == selectedIndex) View.VISIBLE else View.INVISIBLE

        return view
    }

    fun setSelectedIndex(index: Int) {
        selectedIndex = index
        notifyDataSetChanged()
    }
}
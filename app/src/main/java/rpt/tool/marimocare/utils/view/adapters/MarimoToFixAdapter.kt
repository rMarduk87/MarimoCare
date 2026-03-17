package rpt.tool.marimocare.utils.view.adapters

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.MarimoToFix
import java.text.SimpleDateFormat
import java.util.Calendar

class MarimoToFixAdapter(
    private val items: List<MarimoToFix>,
    private val context: Context
) : RecyclerView.Adapter<MarimoToFixAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view as MaterialCardView
        val name: TextView = view.findViewById(R.id.txtName)
        val add: EditText = view.findViewById(R.id.inputAddDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marimo_to_fix, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val marimo = items[position]

        holder.name.text = marimo.name
        holder.add.setText(marimo.registrationDate)

        setupDatePicker(holder, marimo)
    }


    override fun getItemCount() = items.size

    @SuppressLint("SimpleDateFormat")
    private fun setupDatePicker(holder: ViewHolder, marimo: MarimoToFix) {
        holder.add.setOnClickListener {

            val calendar = Calendar.getInstance()

            val dialog = DatePickerDialog(
                context,
                { _, year, month, day ->

                    calendar.set(year, month, day)

                    val newDate = SimpleDateFormat("yyyy-MM-dd")
                        .format(calendar.time)

                    holder.add.setText(newDate)
                    marimo.registrationDate = newDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val maxDate = sdf.parse(marimo.lastWaterChanges)

                maxDate?.let {
                    dialog.datePicker.maxDate = it.time
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialog.setTitle("")
            dialog.show()
        }
    }
}
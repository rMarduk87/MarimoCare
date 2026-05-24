package rpt.tool.marimocare.utils.view.adapters.decoration

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemPotDecorationBinding
import rpt.tool.marimocare.utils.data.appmodels.decoration.PotDecoration
import rpt.tool.marimocare.utils.view.adapters.CustomSpinnerAdapter

class PotDecorationAdapter(
    private val context: Context,
    private var decorations: MutableList<PotDecoration>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<PotDecorationAdapter.ViewHolder>() {

    private val decorationTypes = context.resources.getStringArray(R.array.decoration_types).toList()

    inner class ViewHolder(val binding: ItemPotDecorationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPotDecorationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = decorations[position]
        val binding = holder.binding

        binding.decorationTitle.text = context.getString(R.string.decoration_n, position + 1)

        binding.expandableLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        binding.btnExpandCollapse.setImageResource(
            if (item.isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
        )

        binding.btnExpandCollapse.setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
        }

        binding.btnDelete.setOnClickListener {
            onDelete(position)
        }

        // Setup inputs
        binding.inputDecName.setText(item.name)
        binding.inputDecColour.setText(item.colour)
        binding.inputDecDimensions.setText(item.dimensions)
        binding.inputDecMaterial.setText(item.material)
        binding.inputDecNotes.setText(item.notes)

        updateColorPreview(binding, item.colour)

        // Listeners for changes
        binding.inputDecName.addTextChangedListener(SimpleTextWatcher { item.name = it })
        binding.inputDecColour.addTextChangedListener(SimpleTextWatcher {
            item.colour = it
            updateColorPreview(binding, it)
        })
        binding.inputDecDimensions.addTextChangedListener(SimpleTextWatcher { item.dimensions = it })
        binding.inputDecMaterial.addTextChangedListener(SimpleTextWatcher { item.material = it })
        binding.inputDecNotes.addTextChangedListener(SimpleTextWatcher { item.notes = it })

        // Spinner
        val adapter = CustomSpinnerAdapter(context, decorationTypes)
        binding.typeSpinnerLayout.customSpinner.adapter = adapter
        
        val typeIndex = decorationTypes.indexOf(item.type)
        if (typeIndex >= 0) {
            binding.typeSpinnerLayout.customSpinner.setSelection(typeIndex)
        }

        binding.typeSpinnerLayout.customSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                adapter.setSelectedIndex(pos)
                item.type = decorationTypes[pos]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateColorPreview(binding: ItemPotDecorationBinding, colour: String) {
        try {
            binding.colorPreview.setBackgroundColor(Color.parseColor(colour))
        } catch (e: Exception) {
            binding.colorPreview.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int = decorations.size

    private class SimpleTextWatcher(val onTextChanged: (String) -> Unit) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s.toString())
        }
        override fun afterTextChanged(s: Editable?) {}
    }
}
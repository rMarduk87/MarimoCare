package rpt.tool.marimocare.utils.view.adapters.decoration

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.GridView
import androidx.core.content.ContextCompat
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
    private val colorList = context.resources.getStringArray(R.array.decoration_colors).toList()

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
        binding.inputDecColour.isFocusable = false
        binding.inputDecColour.isFocusableInTouchMode = false
        binding.inputDecColour.isClickable = true
        binding.inputDecDimensions.setText(item.dimensions)
        binding.inputDecMaterial.setText(item.material)
        binding.inputDecNotes.setText(item.notes)

        updateColorPreview(binding, item.colour)

        val onColorClick = View.OnClickListener {
            showColorPicker { selectedColor ->
                item.colour = selectedColor
                binding.inputDecColour.setText(selectedColor)
                updateColorPreview(binding, selectedColor)
            }
        }

        binding.colorPreview.setOnClickListener(onColorClick)
        binding.inputDecColour.setOnClickListener(onColorClick)

        // Listeners for changes
        binding.inputDecName.addTextChangedListener(SimpleTextWatcher { item.name = it })
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
            val drawable = binding.colorPreview.background as? GradientDrawable ?: GradientDrawable()
            drawable.setColor(Color.parseColor(colour))
            drawable.setStroke(2, ContextCompat.getColor(context, R.color.marimo_milestone_bg))
            drawable.cornerRadius = 8f
            binding.colorPreview.background = drawable
        } catch (e: Exception) {
            binding.colorPreview.setBackgroundColor(Color.LTGRAY)
        }
    }

    private fun showColorPicker(onColorSelected: (String) -> Unit) {
        val gridView = GridView(context).apply {
            numColumns = 5
            columnWidth = GridView.AUTO_FIT
            verticalSpacing = 20
            horizontalSpacing = 20
            setPadding(40, 40, 40, 40)
            stretchMode = GridView.STRETCH_COLUMN_WIDTH
            adapter = object : BaseAdapter() {
                override fun getCount(): Int = colorList.size
                override fun getItem(position: Int): Any = colorList[position]
                override fun getItemId(position: Int): Long = position.toLong()
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    val view = convertView ?: View(context).apply {
                        layoutParams = ViewGroup.LayoutParams(100, 100)
                    }
                    val color = colorList[position]
                    val drawable = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(Color.parseColor(color))
                        setStroke(2, Color.LTGRAY)
                    }
                    view.background = drawable
                    return view
                }
            }
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.colour))
            .setView(gridView)
            .setNegativeButton(context.getString(R.string.cancel), null)
            .create()

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            onColorSelected(colorList[position])
            dialog.dismiss()
        }

        dialog.show()
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
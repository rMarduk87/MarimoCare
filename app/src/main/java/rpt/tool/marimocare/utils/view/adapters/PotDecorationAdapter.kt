package rpt.tool.marimocare.utils.view.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemPotDecorationBinding
import rpt.tool.marimocare.utils.data.appmodels.PotDecoration

class PotDecorationAdapter(
    private val context: Context,
    private var decorations: MutableList<PotDecoration>,
    private val onDelete: (Int) -> Unit,
) : RecyclerView.Adapter<PotDecorationAdapter.ViewHolder>() {

    private val decorationTypes = context.resources.getStringArray(R.array.decoration_types).toList()
    private val colorList = context.resources.getStringArray(R.array.decoration_colors).toList()
    private val colorNames = context.resources.getStringArray(R.array.decoration_color_names).toList()

    class ViewHolder(val binding: ItemPotDecorationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPotDecorationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = decorations[position]
        val binding = holder.binding

        // 1. Pulizia dei vecchi TextWatcher (previene bug durante il riciclo delle view)
        clearTextWatchers(binding)

        // 2. Setup iniziale del Titolo Dinamico
        updateDynamicTitle(binding, item, position)

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
            showColorPicker { selectedColor, selectedName ->
                item.colour = selectedColor
                binding.inputDecColour.setText(selectedName)
                updateColorPreview(binding, selectedColor)
                // Aggiorna il titolo quando cambia il colore
                updateDynamicTitle(binding, item, position)
            }
        }

        binding.colorPreview.setOnClickListener(onColorClick)
        binding.inputDecColour.setOnClickListener(onColorClick)

        // Listeners for changes con aggiornamento del titolo e salvataggio dei tag
        val nameWatcher = SimpleTextWatcher {
            item.name = it
            updateDynamicTitle(binding, item, position) // Aggiorna in tempo reale
        }
        binding.inputDecName.addTextChangedListener(nameWatcher)
        binding.inputDecName.tag = nameWatcher

        val dimensionsWatcher = SimpleTextWatcher { item.dimensions = it }
        binding.inputDecDimensions.addTextChangedListener(dimensionsWatcher)
        binding.inputDecDimensions.tag = dimensionsWatcher

        val materialWatcher = SimpleTextWatcher { item.material = it }
        binding.inputDecMaterial.addTextChangedListener(materialWatcher)
        binding.inputDecMaterial.tag = materialWatcher

        val notesWatcher = SimpleTextWatcher { item.notes = it }
        binding.inputDecNotes.addTextChangedListener(notesWatcher)
        binding.inputDecNotes.tag = notesWatcher

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
                // Aggiorna il titolo quando cambia il tipo
                updateDynamicTitle(binding, item, position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Funzione Helper per comporre il titolo: Nome(grassetto) - Tipo - Colore (Icona)
     */
    private fun updateDynamicTitle(binding: ItemPotDecorationBinding, item: PotDecoration, position: Int) {

        // OVERRIDE DELL'XML: Annulliamo il bold globale impostato da android:textStyle="bold" nell'XML
        binding.decorationTitle.typeface = Typeface.DEFAULT

        val defaultName = context.getString(R.string.decoration_n, position + 1)
        val nameToDisplay = if (item.name.isNullOrBlank()) defaultName else item.name

        val typeToDisplay = if (!item.type.isNullOrBlank()) " - ${item.type}" else ""

        val builder = SpannableStringBuilder()

        // 1. Aggiungiamo il Nome
        val startName = builder.length
        builder.append(nameToDisplay)

        // Rendiamo SOLO il Nome in grassetto
        builder.setSpan(StyleSpan(Typeface.BOLD), startName, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 2. Aggiungiamo Tipo
        builder.append(typeToDisplay)

        // 3. Aggiungiamo il Colore come cerchietto colorato (Icon Style) se presente
        if (!item.colour.isNullOrBlank()) {
            builder.append("  ●") // Usiamo un cerchio unicode come base
            try {
                val color = item.colour.toColorInt()
                builder.setSpan(
                    ForegroundColorSpan(color),
                    builder.length - 1,
                    builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // Opzionalmente ingrandiamo un po' il cerchio
                builder.setSpan(
                    RelativeSizeSpan(1.2f),
                    builder.length - 1,
                    builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } catch (_: Exception) {}
        }

        binding.decorationTitle.text = builder
    }

    /**
     * Recupera il nome del colore (se disponibile) o il codice hex
     */
    private fun getColorName(colour: String?): String {
        if (colour.isNullOrBlank()) return ""
        return if (colour.startsWith("#")) {
            val index = colorList.map { it.uppercase() }.indexOf(colour.uppercase())
            if (index >= 0) colorNames[index] else colour
        } else {
            colour
        }
    }

    /**
     * Rimuove i vecchi TextWatcher per prevenire chiamate duplicate riciclando le View
     */
    private fun clearTextWatchers(binding: ItemPotDecorationBinding) {
        (binding.inputDecName.tag as? TextWatcher)?.let { binding.inputDecName.removeTextChangedListener(it) }
        (binding.inputDecDimensions.tag as? TextWatcher)?.let { binding.inputDecDimensions.removeTextChangedListener(it) }
        (binding.inputDecMaterial.tag as? TextWatcher)?.let { binding.inputDecMaterial.removeTextChangedListener(it) }
        (binding.inputDecNotes.tag as? TextWatcher)?.let { binding.inputDecNotes.removeTextChangedListener(it) }
    }

    private fun updateColorPreview(binding: ItemPotDecorationBinding, colour: String?) {
        if (colour.isNullOrBlank()) return
        try {
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(colour.toColorInt())
            drawable.setStroke(2, ContextCompat.getColor(context, R.color.marimo_milestone_bg))
            binding.colorPreview.background = drawable
        } catch (_: Exception) {
            binding.colorPreview.setBackgroundResource(R.drawable.bg_icon_circle)
        }
    }

    private fun showColorPicker(onColorSelected: (String, String) -> Unit) {
        val gridView = GridView(context).apply {
            numColumns = 11
            columnWidth = GridView.AUTO_FIT
            verticalSpacing = 10
            horizontalSpacing = 10
            setPadding(20, 20, 20, 20)
            stretchMode = GridView.STRETCH_COLUMN_WIDTH
            adapter = object : BaseAdapter() {
                override fun getCount(): Int = colorList.size
                override fun getItem(position: Int): Any = colorList[position]
                override fun getItemId(position: Int): Long = position.toLong()
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    val view = convertView ?: View(context).apply {
                        layoutParams = ViewGroup.LayoutParams(50, 50)
                    }
                    val color = colorList[position]
                    val drawable = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(Color.parseColor(color))
                        setStroke(1, Color.LTGRAY)
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
            onColorSelected(colorList[position], colorNames[position])
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
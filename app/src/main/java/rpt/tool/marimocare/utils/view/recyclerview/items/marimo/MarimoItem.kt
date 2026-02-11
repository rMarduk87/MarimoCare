package rpt.tool.marimocare.utils.view.recyclerview.items.marimo

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.bumptech.glide.Glide
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.view.recyclerview.BaseRecyclerViewBindingItem
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import java.io.File

class MarimoItem(var marimo: Marimo) :
    BaseRecyclerViewBindingItem<ItemMarimoBinding>(ItemMarimoBinding::inflate) {

    override val type: Int = R.id.rv_marimo
    private var binding: ItemMarimoBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun bindView(binding: ItemMarimoBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)

        updateUI(binding)
        this.binding = binding
    }

    override fun unbindView(binding: ItemMarimoBinding) {
        super.unbindView(binding)
        this.binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(binding: ItemMarimoBinding) {
        val context = binding.root.context
        val res = binding.root.resources
        val daysLeft = marimo.daysLeft
        val status = MarimoStatus.from(daysLeft)

        binding.txtName.text = marimo.name
        binding.txtFrequency.text = res.getString(R.string.changes_every_days,
            marimo.changeFrequencyDays)
        binding.txtLastChange.text = marimo.lastChanged
        binding.txtNextChange.text = marimo.nextChange
        binding.txtNotes.text = marimo.notes ?: res.getString(R.string.no_notes)
        binding.txtDaysLeft.text = status.formatDaysLeftText(res, daysLeft)

        binding.txtNextChange.setTextColor(ContextCompat.getColor(context, status.color))
        binding.txtDaysLeft.setTextColor(ContextCompat.getColor(context, status.color))
        binding.txtDaysLeftIcon.setImageResource(status.icon)
        binding.layoutText.setBackgroundResource(status.daysLeftBackground)
        binding.cardMarimo.setBackgroundResource(status.cardBackground)

        setImageForIcon(binding.imgMarimoIcon,status)


        binding.btnWaterChanged.setBackgroundResource(status.buttonChangeBg)
        binding.btnEdit.setBackgroundResource(status.buttonEditBg)
        binding.btnDelete.setBackgroundResource(status.buttonDeleteBg)
        binding.btnDetails.setBackgroundResource(status.buttonDetailBg)

        binding.cardNotes.setBackgroundResource(status.notesCardBg)
        binding.cardDate.setBackgroundResource(status.cardDateBg)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun update(newMarimo: Marimo) {
        this.marimo = newMarimo
        binding?.let { updateUI(it) }
    }

    fun setImageForIcon(imgMarimoIcon: ImageView, status: MarimoStatus) {

        val context = imgMarimoIcon.context

        imgMarimoIcon.setImageDrawable(null)
        imgMarimoIcon.background = null
        imgMarimoIcon.clearColorFilter()
        imgMarimoIcon.imageTintList = null

        val photoPath = marimo.photo

        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)

            if (file.exists() && file.length() > 0) {

                Glide.with(imgMarimoIcon)
                    .load(file)
                    .centerCrop()
                    .placeholder(R.drawable.ic_water_drop_white)
                    .error(R.drawable.ic_water_drop_white)
                    .into(imgMarimoIcon)

                return
            }
        }

        imgMarimoIcon.apply {
            setImageResource(status.dropIcon)
            background = ContextCompat.getDrawable(context, status.dropCircle)
        }
    }
}
package rpt.tool.marimocare.utils.view.recyclerview.items.marimo

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import rpt.tool.marimocare.utils.view.recyclerview.BaseRecyclerViewBindingItem
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo


class MarimoItem(val marimo: Marimo) :
    BaseRecyclerViewBindingItem<ItemMarimoBinding>(ItemMarimoBinding::inflate) {

    override val type: Int = R.id.rv_marimo



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun bindView(binding: ItemMarimoBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)

        val daysLeft = marimo.daysLeft

        binding.cardMarimo.background = ContextCompat.getDrawable(binding.root.context, R.drawable.bg_card_marimo)
        binding.txtName.text = marimo.name
        binding.txtFrequency.text =
            binding.root.resources.getString(R.string.changes_every_days, marimo.changeFrequencyDays)
        binding.txtLastChange.text = marimo.lastChanged
        binding.txtNextChange.text = marimo.nextChange

        val color = if(daysLeft>0) binding.root.context.getColor(R.color.marimo_item_green)
        else if(daysLeft==0) binding.root.context.getColor(R.color.marimo_orange) else
            binding.root.context.getColor(R.color.marimo_red)

        setMarimoIcon(daysLeft,binding)

        binding.txtNextChange.setTextColor(color)
        binding.txtNotes.text = marimo.notes ?: binding.root.resources.getString(R.string.no_notes)


        binding.txtDaysLeft.text = setText(daysLeft,binding.root.resources)

        binding.txtDaysLeft.setTextColor(color)

        binding.txtDaysLeftIcon.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
            getIcon(daysLeft)))

        val idBackground = setBackground(daysLeft)
        binding.layoutText.background = ContextCompat.getDrawable(binding.root.context,
            idBackground)

        binding.btnWaterChanged.background = if(daysLeft>=0) ContextCompat.getDrawable(binding.root.context,
            R.drawable.bg_btn_primary)  else ContextCompat.getDrawable(binding.root.context,
            R.drawable.bg_btn_red)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIcon(daysLeft: Int): Int {
        var id = R.drawable.ic_clock
        val isSoon = AppUtils.isDueSoon(daysLeft)
        if (daysLeft < 0 && !isSoon){
            id = R.drawable.ic_warning_triangle_red
        }
        if(daysLeft == 0 && isSoon){
            id =  R.drawable.ic_calendar_orange
        }
        return id
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setBackground(daysLeft: Int) : Int {
        var id = R.drawable.bg_days_left_pill
        val isSoon = AppUtils.isDueSoon(daysLeft)
        if (daysLeft < 0 && !isSoon){
            id = R.drawable.bg_days_left_pill_overdue
        }
        if(daysLeft == 0 && isSoon){
            id =  R.drawable.bg_days_left_pill_due_soon
        }
        return id
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMarimoIcon(daysLeft: Int, binding: ItemMarimoBinding) {
        var icon = R.drawable.ic_water_drop_green
        var circle = R.drawable.bg_circle_green
        val isSoon = AppUtils.isDueSoon(daysLeft)
        if (daysLeft < 0 && !isSoon){
            icon = R.drawable.ic_water_drop_red
            circle = R.drawable.bg_circle_red
        }
        if(daysLeft == 0 && isSoon){
            icon =  R.drawable.ic_water_drop_orange
            circle = R.drawable.bg_circle_orange
        }

        binding.imgMarimoIcon.setImageDrawable(ContextCompat.getDrawable(binding.root.context,
            icon))
        binding.imgMarimoIcon.background = ContextCompat.getDrawable(binding.root.context,
            circle)

    }

    private fun setText(daysLeft: Int, resources: Resources): String {
        if(daysLeft < 0){
            return (daysLeft*-1).toString() + " " +
                    resources.getString(R.string.days_overdue)
        }
        if(daysLeft > 0){
            return daysLeft.toString() + " " +
                    resources.getString(R.string.days_left)
        }

        return resources.getString(R.string.due_in) + " " + 0.toString() + " " +
                resources.getString(R.string.days)
    }

    override fun unbindView(binding: ItemMarimoBinding) {

    }
}
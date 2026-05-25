package rpt.tool.marimocare.utils.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemWaterMilestoneBinding
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import java.io.File

class CareTimeLineAdapter(
    private val items: List<MarimoChange>
) : RecyclerView.Adapter<CareTimeLineAdapter.MarimoChangeViewHolder>() {

    class MarimoChangeViewHolder(
        private val binding: ItemWaterMilestoneBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MarimoChange) = with(binding) {

            cardDate.text = item.waterChangeData ?: ""

            // NOTE
            if (!item.waterChangesLog.isNullOrEmpty()) {
                notes.visibility = View.VISIBLE
                notes.text = item.waterChangesLog
            } else {
                notes.visibility = View.GONE
            }

            if (!item.waterChangeImage.isNullOrEmpty()) {
                waterOrMilestoneImage.visibility = View.VISIBLE

                Glide.with(root.context)
                    .load(item.waterChangeImage)
                    .centerCrop()
                    .into(waterOrMilestoneImage)

            } else {
                waterOrMilestoneImage.visibility = View.GONE
            }

            if (item.isMilestone) {

                cardTitle.setText(R.string.milestone)

                timelineIconContainer.background =
                    ContextCompat.getDrawable(root.context, R.drawable.bg_badge_orange)

                timelineIconContainer.backgroundTintList =
                    ContextCompat.getColorStateList(root.context, R.color.marimo_orange)
                        ?: android.content.res.ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.marimo_bg_warning))

                val timelineIcon =
                    timelineIconContainer.getChildAt(0) as
                            androidx.appcompat.widget.AppCompatImageView

                timelineIcon.setImageResource(R.drawable.ic_star_outline)
                timelineIcon.imageTintList =
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.marimo_tint_warning))

                cardIcon.visibility = View.VISIBLE
                cardIcon.setText(R.string.star)

            }

            else {

                cardTitle.setText(R.string.waters)

                timelineIconContainer.background =
                    ContextCompat.getDrawable(root.context, R.drawable.bg_badge_green)

                timelineIconContainer.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.marimo_item_board))

                val timelineIcon =
                    timelineIconContainer.getChildAt(0) as
                            androidx.appcompat.widget.AppCompatImageView

                timelineIcon.setImageResource(R.drawable.ic_water_drop_green)
                timelineIcon.imageTintList =
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.green))

                cardIcon.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarimoChangeViewHolder {
        val binding = ItemWaterMilestoneBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MarimoChangeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarimoChangeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
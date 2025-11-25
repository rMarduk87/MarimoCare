package rpt.tool.marimocare.utils.view.viewpager.tips

import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class TipsPagerAdapter(
    private val tips: List<String>
) : RecyclerView.Adapter<TipsPagerAdapter.TipViewHolder>() {

    inner class TipViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val tv = TextView(parent.context).apply {
            setTextColor("#0B6E4F".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                marginStart = (36 * resources.displayMetrics.density).toInt()
            }
        }
        return TipViewHolder(tv)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.tv.text = tips[position]
    }

    override fun getItemCount() = tips.size
}
package rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks

import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.view.getFastAdapterItemViewBinding
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem

class ChangeWaterEventHook(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val onWaterChanged: () -> Unit
) : ClickEventHook<MarimoItem>() {

    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        val binding = viewHolder.getFastAdapterItemViewBinding<ItemMarimoBinding>()
        return binding?.btnWaterChanged
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<MarimoItem>,
        item: MarimoItem,
    ) {

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val marimo = RepositoryManager.marimoRepository.getMarimo(item.marimo.code)
            if (marimo != null) {

                val lastChanged = AppUtils.getCurrentDate()
                RepositoryManager.marimoRepository.updateWaterMarimo(lastChanged, item.marimo.code)

                val updated = RepositoryManager.marimoRepository.getMarimo(
                    item.marimo.code)

                withContext(Dispatchers.IO) {
                    AlertDataUtils.recalc(context)
                }

                withContext(Dispatchers.Main) {
                    if (updated != null) {
                        item.update(updated)
                        fastAdapter.notifyAdapterItemChanged(position)
                    }

                    onWaterChanged()
                }
            }
        }
    }
}
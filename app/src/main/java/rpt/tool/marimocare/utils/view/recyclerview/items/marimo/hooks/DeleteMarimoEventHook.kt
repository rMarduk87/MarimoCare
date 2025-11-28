package rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.view.getFastAdapterItemViewBinding
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem

class DeleteMarimoEventHook(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val onDelete: () -> Unit
) : ClickEventHook<MarimoItem>() {

    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        val binding = viewHolder.getFastAdapterItemViewBinding<ItemMarimoBinding>()
        return binding?.btnDelete
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<MarimoItem>,
        item: MarimoItem,
    ) {
        showDeleteMarimoDialog(item, fastAdapter, position)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDeleteMarimoDialog(
        item: MarimoItem,
        fastAdapter: FastAdapter<MarimoItem>,
        position: Int
    ) {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_delete_marimo, null)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)

        tvTitle.text = buildString {
            append(context.getString(R.string.delete))
            append(item.marimo.name)
            append("?")
            tvMessage.text = context.getString(
                R.string
                    .are_you_sure_you_want_to_delete_this_marimo_this_action_cannot_be_undone
            )

            val dialog = MaterialAlertDialogBuilder(
                context,
                com.google.android.material
                    .R.style.Theme_Material3_DayNight_Dialog_Alert
            )
                .setView(view)
                .create()

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnDelete.setOnClickListener {
                dialog.dismiss()
                deleteMarimo(item, fastAdapter, position)
            }
            dialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteMarimo(item: MarimoItem, fastAdapter: FastAdapter<MarimoItem>, position: Int) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val marimo = RepositoryManager.marimoRepository.getMarimo(item.marimo.code)
            if (marimo != null) {

                RepositoryManager.marimoRepository.deleteMarimo(item.marimo.code)

                withContext(Dispatchers.IO) {
                    AlertDataUtils.recalc(context)
                }

                withContext(Dispatchers.Main) {
                    fastAdapter.notifyAdapterItemChanged(position)

                    onDelete()
                }
            }
        }
    }
}
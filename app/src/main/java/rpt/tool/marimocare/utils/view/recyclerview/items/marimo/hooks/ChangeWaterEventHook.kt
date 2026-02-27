package rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemMarimoBinding
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.view.getFastAdapterItemViewBinding
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.textfield.TextInputEditText
import androidx.core.net.toUri

class ChangeWaterEventHook(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val onWaterChanged: () -> Unit,
    private val onPickImage: (callback: (String?) -> Unit) -> Unit
) : ClickEventHook<MarimoItem>() {

    private var imagePath: String? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var tempImageUri: Uri? = null

    init {
        if (lifecycleOwner is ActivityResultCaller) {

            cameraLauncher = lifecycleOwner.registerForActivityResult(
                ActivityResultContracts.TakePicture()
            ) { success ->
                if (success) {
                    imagePath = tempImageUri?.path
                }
            }

            galleryLauncher = lifecycleOwner.registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    imagePath = it.toString()
                }
            }
        }
    }

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

        showLogDialog(item, fastAdapter, position, context)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showLogDialog(
        item: MarimoItem,
        fastAdapter: FastAdapter<MarimoItem>,
        position: Int,
        context: Context
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_log_water_change)

        dialog.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val etNotes = dialog.findViewById<TextInputEditText>(R.id.etNotes)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)
        val checkboxMilestone = dialog.findViewById<CheckBox>(R.id.checkboxMilestone)
        val uploadContainer = dialog.findViewById<View>(R.id.uploadContainer)
        val imagePreview = dialog.findViewById<ImageView>(R.id.imagePreview)

        var imagePath: String? = null

        uploadContainer?.setOnClickListener {

            onPickImage { selectedPath ->
                imagePath = selectedPath

                selectedPath?.let {
                    imagePreview?.setImageURI(it.toUri())
                }
            }
        }

        btnClose?.setOnClickListener { dialog.dismiss() }
        btnCancel?.setOnClickListener { dialog.dismiss() }

        btnSave?.setOnClickListener {

            val notes = etNotes?.text?.toString()?.trim()
            val isMilestone = checkboxMilestone?.isChecked ?: false

            updateMarimo(
                item = item,
                fastAdapter = fastAdapter,
                position = position,
                notes = if (notes.isNullOrEmpty()) null else notes,
                imagePath = imagePath,
                isMilestone = isMilestone
            )

            dialog.dismiss()
        }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMarimo(
        item: MarimoItem,
        fastAdapter: FastAdapter<MarimoItem>,
        position: Int,
        notes: String?,
        imagePath: String?,
        isMilestone: Boolean
    ) {
        lifecycleOwner.lifecycleScope.launch {

            val updated = withContext(Dispatchers.IO) {

                val marimo = RepositoryManager.marimoRepository
                    .getMarimo(item.marimo.code)

                if (marimo != null) {

                    val lastChanged = AppUtils.getCurrentDate()

                    RepositoryManager.marimoRepository.updateWaterMarimo(
                        lastChanged,
                        item.marimo.code
                    )

                    RepositoryManager.marimoRepository.addWaterChanges(
                        item.marimo.code,
                        lastChanged,
                        notes,
                        imagePath,
                        isMilestone
                    )

                    RepositoryManager.marimoRepository.updateHealthScore(
                        item.marimo.code,
                        lastChanged,100)

                    AlertDataUtils.recalc(context)

                    RepositoryManager.marimoRepository
                        .getMarimo(item.marimo.code)

                } else null
            }

            if (updated != null) {
                item.update(updated)
                fastAdapter.notifyAdapterItemChanged(position)
            }

            onWaterChanged()
        }
    }
}
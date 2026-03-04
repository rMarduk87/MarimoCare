package rpt.tool.marimocare.ui.marimo.details

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentMarimoDetailsBinding
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScore
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import rpt.tool.marimocare.utils.view.adapters.CareTimeLineAdapter
import java.io.File
import kotlin.getValue

class MarimoDetailsFragment : BaseFragment<FragmentMarimoDetailsBinding>(
    FragmentMarimoDetailsBinding::inflate) {

    private var marimoCode: Int = 0
    private val args: MarimoDetailsFragmentArgs by navArgs()
    private var marimo: Marimo? = null
    private var dataToAdapter: MutableList<MarimoChange> = mutableListOf()
    private lateinit var adapter: CareTimeLineAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()

        marimoCode = args.MarimoCode

        addDataToMarimo(marimoCode)

        binding.include.appLogo.setOnClickListener {
            safeNavController?.safeNavigate(
                MarimoDetailsFragmentDirections
                    .actionMarimoDetailFragmentToDashboardFragment()
            )
        }
    }

    private fun setupHeaderButtons() {
        HeaderHelper.setupHeaderButtons(
            requireContext(),
            listOf(
                HeaderButtonConfig(
                    button = binding.include.btnDashboardHeader,
                    iconRes = R.drawable.ic_dashboard,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.dashboard),
                    onClick = {
                        safeNavController?.safeNavigate(
                            MarimoDetailsFragmentDirections
                                .actionMarimoDetailFragmentToDashboardFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    enabled = true,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
                    onClick = {
                        safeNavController?.safeNavigate(
                            MarimoDetailsFragmentDirections
                                .actionMarimoDetailFragmentToAddOrEditFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    onClick = {
                        safeNavController?.safeNavigate(
                            MarimoDetailsFragmentDirections
                                .actionMarimoDetailFragmentToSettingsFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.stats),
                    onClick = {
                        safeNavController?.safeNavigate(
                            MarimoDetailsFragmentDirections
                                .actionMarimoDetailFragmentToStatsFragment()
                        )
                    }
                )
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDataToMarimo(marimoCode: Int) {
        if (marimoCode != 0) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                marimo = RepositoryManager.marimoRepository.getMarimo(marimoCode)
                val changes = RepositoryManager.marimoRepository.getMarimoTotalWaterChanged(marimoCode)
                val milestones = RepositoryManager.marimoRepository.getMarimoTotalMilestones(marimoCode)
                val marimoChanges = RepositoryManager.marimoRepository.getAllChanges(marimoCode)
                val marimoHealths = RepositoryManager.marimoRepository.getAllHealth(marimoCode)
                val marimoHealth = RepositoryManager.marimoRepository.getSpecificHealth(
                    marimoCode,null
                )
                withContext(Dispatchers.Main) {
                    if (marimo != null) {
                        binding.marimoName.text = marimo!!.name
                        includeProfile(marimo,changes,milestones)
                        includeHealthScore(marimoHealth,marimo)
                        includeLogChanges(marimo)
                        includeCareTimeLine(marimoChanges)
                        includeHealthGraph(marimoHealths)
                    }
                }
            }
        }
    }

    private fun includeProfile(marimo: Marimo?, changes: Int, milestones: Int) {
        binding.include1.marimoCard.setBackgroundResource(R.drawable.bg_card_marimo)
        if (!marimo!!.photo.isNullOrEmpty()) {
            showMarimoImage(File(marimo.photo!!))
        } else {
            showMarimoImage(null)
        }

        binding.include1.txtFrequency.text = buildString {
            append(marimo.changeFrequencyDays.toString())
            append(" ")
            append(getString(R.string.days))
        }
        binding.include1.txtTotalChanges.text = changes.toString()
        binding.include1.txtMilestones.text = milestones.toString()
    }

    private fun showMarimoImage(file: File?) {
        val imageView = binding.include1.imgIcon

        imageView.clearColorFilter()
        imageView.imageTintList = null
        imageView.background = null

        if (file != null && file.exists()) {
            Glide.with(this)
                .load(file)
                .centerCrop()
                .placeholder(R.drawable.ic_water_drop_white)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_water_drop_white)
            imageView.setColorFilter("#00BFA6".toColorInt(), PorterDuff.Mode.SRC_IN)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun includeHealthScore(marimoHealth: Int, marimo: Marimo?) {
        binding.include2.healthValue.text = marimoHealth.toString()
        binding.include2.healthTotal.text = getString(R.string.out_of_100)
        calculateHealthColor(marimoHealth)
        calculateHealthColorText(marimoHealth)
        binding.include2.txtLastChange.text = marimo!!.lastChanged
        binding.include2.txtNextChange.text = marimo.nextChange
        binding.include2.txtNextChange.setTextColor(getColorFromData(marimo.nextChange))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getColorFromData(nextChange: String):Int {
        val differenceBetweenDate = AppUtils.getDifferenceBetweenDates(nextChange,
            AppUtils.getCurrentDate())
        return when(differenceBetweenDate) {
            0 -> requireContext().getColor(R.color.marimo_orange)
            in 1..Int.MAX_VALUE -> requireContext().getColor(R.color.marimo_item_green)
            else -> requireContext().getColor(R.color.marimo_red)
        }
    }

    private fun calculateHealthColor(health: Int) {
        val themeColorHex = when {
            health == 100 -> "#2E7D32"
            health in 40..70 -> "#FFFACD"
            health in 1..19 -> "#F57C00"
            health <= 0 -> "#C62828"
            else -> "#4CAF50"
        }

        val themeColor = themeColorHex.toColorInt()

        binding.include2.healthContainer.backgroundTintList = ColorStateList.valueOf(themeColor)

        binding.include2.marimoCard.setCardBackgroundColor(Color.WHITE)

        binding.include2.marimoCard.strokeColor = themeColor
    }

    private fun calculateHealthColorText(health: Int) {
        val textColor = if(health in 40..70) R.color.marimo_dark else android.R.color.white
        binding.include2.healthValue.setTextColor(resources.getColor(textColor))
        binding.include2.healthTotal.setTextColor(resources.getColor(textColor))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun includeLogChanges(marimo: Marimo?) {
        binding.include3.marimoCard.setBackgroundResource(R.drawable.bg_card_marimo)
        binding.include3.tvNotesContent.text = marimo!!.notes
        binding.include3.btnLogWaterChange.setOnClickListener {
            showLogDialog(marimo)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showLogDialog(
        item: Marimo
    ) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_log_water_change)

        dialog.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        // ---- Views ----
        val etNotes = dialog.findViewById<TextInputEditText>(R.id.etNotes)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)
        val checkboxMilestone = dialog.findViewById<CheckBox>(R.id.checkboxMilestone)
        val uploadContainer = dialog.findViewById<View>(R.id.uploadContainer)
        val imagePreview = dialog.findViewById<ImageView>(R.id.imagePreview)

        var imagePath: String? = null

        /*uploadContainer?.setOnClickListener {

            onPickImage { selectedPath ->
                imagePath = selectedPath

                selectedPath?.let {
                    imagePreview?.setImageURI(it.toUri())
                }
            }
        }*/

        btnClose?.setOnClickListener { dialog.dismiss() }
        btnCancel?.setOnClickListener { dialog.dismiss() }

        btnSave?.setOnClickListener {

            val notes = etNotes?.text?.toString()?.trim()
            val isMilestone = checkboxMilestone?.isChecked ?: false

            updateMarimo(
                marimo = item,
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
        marimo: Marimo,
        notes: String?,
        imagePath: String?,
        isMilestone: Boolean
    ) {
        viewLifecycleOwner.lifecycleScope.launch {

            val updated = withContext(Dispatchers.IO) {

                run {

                    val lastChanged = AppUtils.getCurrentDate()

                    RepositoryManager.marimoRepository.updateWaterMarimo(
                        lastChanged,
                        marimo.code
                    )

                    RepositoryManager.marimoRepository.addWaterChanges(
                        marimo.code,
                        lastChanged,
                        notes,
                        imagePath,
                        isMilestone
                    )

                    AlertDataUtils.recalc(requireContext())

                    RepositoryManager.marimoRepository
                        .getMarimo(marimo.code)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun includeCareTimeLine(marimo: List<MarimoChange>) {
        binding.include4.marimoCard.setBackgroundResource(R.drawable.bg_card_marimo)
        dataToAdapter.apply {
            clear()
            addAll(marimo.map {
                MarimoChange(it.code, it.coderMarimo, it.waterChangeData, it.waterChangesLog,
                    it.waterChangeImage, it.isMilestone) })
        }

        adapter = CareTimeLineAdapter(dataToAdapter)
        adapter.notifyDataSetChanged()
        binding.include4.marimoRecycler.layoutManager =
            LinearLayoutManager(requireContext())
        binding.include4.marimoRecycler.adapter = adapter
    }
    private fun includeHealthGraph(marimo: List<MarimoHealthScore>) {

    }
}
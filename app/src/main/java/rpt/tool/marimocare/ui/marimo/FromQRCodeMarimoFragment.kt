package rpt.tool.marimocare.ui.marimo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentFromQrCodeBinding
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper

class FromQRCodeMarimoFragment :
    BaseFragment<FragmentFromQrCodeBinding>(FragmentFromQrCodeBinding::inflate) {

    private var marimo: Marimo? = null
    private var code: String? = null
    private var name: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()
        code = arguments?.getString("code")
        name = arguments?.getString("name")

        initializeCard(code!!.toInt())

    }

    private fun setupHeaderButtons()
    {
        HeaderHelper.setupHeaderButtons(
            requireContext(),
            listOf(
                HeaderButtonConfig(
                    button = binding.include1.btnDashboardHeader,
                    iconRes = R.drawable.ic_dashboard,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.dashboard),
                    onClick = {
                        safeNavController?.safeNavigate(
                            FromQRCodeMarimoFragmentDirections.
                            actionFromQRCodeMarimoFragmentToDashboardFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
                    onClick = {
                        safeNavController?.safeNavigate(
                            FromQRCodeMarimoFragmentDirections
                                .actionFromQRCodeMarimoFragmentToAddOrEditFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    onClick = {
                        safeNavController?.safeNavigate(
                            FromQRCodeMarimoFragmentDirections
                                .actionFromQRCodeMarimoFragmentToSettingsFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.stats),
                    onClick = {
                        safeNavController?.safeNavigate(
                            FromQRCodeMarimoFragmentDirections
                                .actionFromQRCodeMarimoFragmentToStatsFragment()
                        )
                    }
                )
            )
        )
    }

    private fun initializeCard(code: Int) {
        if (code != 0) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                marimo = RepositoryManager.marimoRepository.getMarimo(code)
                withContext(Dispatchers.Main) {
                    updateUI(marimo)
                }
            }
        }
    }

    private fun updateUI(marimo: Marimo?) {
        if (marimo != null) {
            val daysLeft = marimo.daysLeft
            val status = MarimoStatus.from(daysLeft)

            binding.includeM.txtName.text = marimo.name
            binding.includeM.txtFrequency.text = requireContext()
                .getString(R.string.changes_every_days,
                    marimo.changeFrequencyDays)
            binding.includeM.txtLastChange.text = marimo.lastChanged
            binding.includeM.txtNextChange.text = marimo.nextChange
            binding.includeM.txtNotes.text = marimo.notes ?: requireContext()
                .getString(R.string.no_notes)
            binding.includeM.txtDaysLeft.text = status.formatDaysLeftText(
                requireContext().resources, daysLeft)

            binding.includeM.txtNextChange.setTextColor(ContextCompat.getColor(
                requireContext(), status.color))
            binding.includeM.txtDaysLeft.setTextColor(ContextCompat.getColor(
                requireContext(), status.color))
            binding.includeM.txtDaysLeftIcon.setImageResource(status.icon)
            binding.includeM.layoutText.setBackgroundResource(status.daysLeftBackground)
            binding.includeM.cardMarimo.setBackgroundResource(status.cardBackground)

            binding.includeM.imgMarimoIcon.apply {
                setImageResource(status.dropIcon)
                background = ContextCompat.getDrawable(context, status.dropCircle)
            }

            binding.includeM.btnWaterChanged.setBackgroundResource(status.buttonChangeBg)
            binding.includeM.btnEdit.setBackgroundResource(status.buttonEditBg)
            binding.includeM.btnDelete.setBackgroundResource(status.buttonDeleteBg)

            binding.includeM.cardNotes.setBackgroundResource(status.notesCardBg)
            binding.includeM.cardDate.setBackgroundResource(status.cardDateBg)

            binding.includeM.btnWaterChanged.setOnClickListener {
                waterChange(marimo)
            }

            binding.includeM.btnEdit.setOnClickListener {
                editMarimo(marimo)
            }

            binding.includeM.btnDelete.setOnClickListener {
                deleteMarimoDialog(marimo)
            }
        }
    }

    private fun waterChange(marimo: Marimo?) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val marimo = RepositoryManager.marimoRepository.getMarimo(marimo!!.code)
            val lastChanged = AppUtils.getCurrentDate()
            RepositoryManager.marimoRepository.updateWaterMarimo(lastChanged, marimo!!.code)

            val updated = RepositoryManager.marimoRepository.getMarimo(
                marimo.code)

            withContext(Dispatchers.IO) {
                AlertDataUtils.recalc(requireContext())
                updateUI(updated)
            }
        }
    }



    private fun editMarimo(marimo: Marimo?) {
        safeNavController?.safeNavigate(
            FromQRCodeMarimoFragmentDirections
                .actionFromQRCodeMarimoFragmentToAddOrEditFragment(marimo!!.code)
        )
    }

    private fun deleteMarimoDialog(marimo: Marimo?) {
        showDeleteMarimoDialog(marimo)
    }

    private fun showDeleteMarimoDialog(
        item: Marimo?
    ) {

        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_delete_marimo, null)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)

        tvTitle.text = buildString {
            append(requireContext().getString(R.string.delete))
            append(item!!.name)
            append("?")
            tvMessage.text = requireContext().getString(
                R.string
                    .are_you_sure_you_want_to_delete_this_marimo_this_action_cannot_be_undone
            )

            val dialog = MaterialAlertDialogBuilder(
                requireContext(),
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
                deleteMarimo(item)
            }
            dialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteMarimo(item: Marimo?) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val marimo = RepositoryManager.marimoRepository.getMarimo(item!!.code)
            if (marimo != null) {

                RepositoryManager.marimoRepository.deleteMarimo(item.code)

                withContext(Dispatchers.IO) {
                    AlertDataUtils.recalc(requireContext())
                }
            }
        }
    }
}
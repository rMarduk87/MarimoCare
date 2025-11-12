package rpt.tool.marimocare.ui.marimo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentAddOrEditBinding
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import rpt.tool.marimocare.utils.view.adapters.CustomSpinnerAdapter
import java.text.SimpleDateFormat
import java.util.Calendar

class AddOrEditMarimoFragment :
    BaseFragment<FragmentAddOrEditBinding>(FragmentAddOrEditBinding::inflate) {

    private var freq: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()
        setupSpinner()
        setupDatePicker()
        setupActionButtons()
    }

    private fun setupHeaderButtons() {
        HeaderHelper.setupHeaderButtons(
            requireContext(),
            listOf(
                HeaderButtonConfig(
                    button = binding.include1.btnDashboardHeader,
                    iconRes = R.drawable.ic_dashboard,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    onClick = {
                        safeNavController?.safeNavigate(
                            AddOrEditMarimoFragmentDirections.actionAddOrEditFragmentToDashboardFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    enabled = false
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    onClick = {
                        safeNavController?.safeNavigate(
                            AddOrEditMarimoFragmentDirections.actionAddOrEditFragmentToSettingsFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    onClick = {
                        safeNavController?.safeNavigate(
                            AddOrEditMarimoFragmentDirections.actionAddOrEditFragmentToStatsFragment()
                        )
                    }
                )
            )
        )
    }

    private fun setupSpinner() {
        val frequencies = resources.getStringArray(R.array.marimo_frequencies).toList()
        val spinner = binding.marimoSpinnerLayout.customSpinner
        val arrow = binding.marimoSpinnerLayout.arrow
        val adapter = CustomSpinnerAdapter(requireContext(), frequencies)
        spinner.adapter = adapter
        spinner.setPopupBackgroundDrawable(ContextCompat.getDrawable(requireContext(),
            R.drawable.spinner_dropdown_background))

        val rotateUp = AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_up)
        val rotateDown = AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_down)

        spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) arrow.startAnimation(rotateUp)
            false
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int,
                                        id: Long) {
                adapter.setSelectedIndex(position)
                arrow.startAnimation(rotateDown)
                freq = AppUtils.extractDay(frequencies[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                arrow.startAnimation(rotateDown)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupDatePicker() {
        binding.inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.inputDate.setText(SimpleDateFormat("yyyy-MM-dd").
                    format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = AppUtils.getMaxDate()
                setTitle("")
                show()
            }
        }
    }

    private fun setupActionButtons() {
        binding.btnAdd.setOnClickListener { saveMarimoIfValid() }
        binding.btnCancel.setOnClickListener { clearAll() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMarimoIfValid() {
        val name = binding.inputName.text.toString()
        val lastWater = binding.inputDate.text.toString()
        val notes = binding.inputNotes.text.toString()

        if (name.isBlank() || lastWater.isBlank()) {
            Toast.makeText(requireContext(), getString(
                R.string.please_fill_all_the_fields), Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            RepositoryManager.marimoRepository.addMarimo(name,
                lastWater, notes, freq)
        }
        clearAll()
    }

    private fun clearAll() {
        binding.inputName.text.clear()
        binding.inputDate.text.clear()
        binding.inputNotes.text.clear()
        binding.marimoSpinnerLayout.customSpinner.setSelection(0)
    }
}
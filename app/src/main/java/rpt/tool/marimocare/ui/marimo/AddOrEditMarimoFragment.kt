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
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentAddOrEditBinding
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
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

    private lateinit var frequencies: List<String>
    private var freq: Int = 0
    private var marimoCode: Int = 0
    private val args: AddOrEditMarimoFragmentArgs by navArgs()
    private var marimo: Marimo? = null



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        frequencies = resources.getStringArray(R.array.marimo_frequencies).toList()

        setupHeaderButtons()
        setupSpinner(frequencies)
        setupDatePicker()

        marimoCode = args.MarimoCode

        addDataToMarimo(marimoCode)

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
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.dashboard),
                    onClick = {
                        safeNavController?.safeNavigate(
                            AddOrEditMarimoFragmentDirections
                                .actionAddOrEditFragmentToDashboardFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    enabled = false,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
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
                            AddOrEditMarimoFragmentDirections
                                .actionAddOrEditFragmentToSettingsFragment()
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
                            AddOrEditMarimoFragmentDirections
                                .actionAddOrEditFragmentToStatsFragment()
                        )
                    }
                )
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSpinner(frequencies: List<String>) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupActionButtons(marimo: Marimo?) {
        binding.btnAdd.setOnClickListener { saveMarimoIfValid(marimo) }
        binding.btnCancel.setOnClickListener { clearAll() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMarimoIfValid(marimo: Marimo?) {
        val name = binding.inputName.text.toString()
        val lastWater = binding.inputDate.text.toString()
        val notes = binding.inputNotes.text.toString()

        if (name.isBlank() || lastWater.isBlank()) {
            Toast.makeText(requireContext(), getString(
                R.string.please_fill_all_the_fields),
                Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            if(marimo != null) {
                RepositoryManager.marimoRepository.updateMarimo(
                    marimo.code,
                    name,
                    lastWater, notes, freq
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(
                        R.string.updated_ok),
                        Toast.LENGTH_SHORT).show()
                }

            }
            else{
                RepositoryManager.marimoRepository.addMarimo(name,
                    lastWater, notes, freq)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(
                        R.string.new_marimo_added),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
        clearAll()
    }

    private fun clearAll() {
        binding.inputName.text.clear()
        binding.inputDate.text.clear()
        binding.inputNotes.text.clear()
        binding.marimoSpinnerLayout.customSpinner.setSelection(0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDataToMarimo(marimoCode: Int) {
        if(marimoCode != 0){
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                marimo = RepositoryManager.marimoRepository.getMarimo(marimoCode)
                if(marimo != null) {
                    withContext(Dispatchers.Main) {
                        binding.inputName.setText(marimo!!.name)
                        binding.inputDate.setText(marimo!!.lastChanged)
                        binding.inputNotes.setText(marimo!!.notes)
                        val index = AppUtils.indexOfContaining(
                            marimo!!.changeFrequencyDays.toString(),frequencies)
                        binding.marimoSpinnerLayout.customSpinner.setSelection(if(index != -1)
                            index else index)
                        binding.btnAdd.text = getString(R.string.update)
                    }
                }
            }
        }

        setupActionButtons(marimo)
    }
}
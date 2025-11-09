package rpt.tool.marimocare.ui.marimo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentAddOrEditBinding
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.adapters.CustomSpinnerAdapter
import java.text.SimpleDateFormat
import java.util.Calendar


class AddOrEditMarimoFragment:
    BaseFragment<FragmentAddOrEditBinding>(FragmentAddOrEditBinding::inflate) {

    var freq : Int = 0
    var marimoName: String = ""
    var lastWaterChange: String = ""
    var notes: String = ""


    @SuppressLint("ClickableViewAccessibility", "DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isSpinnerOpen = false
        val rotateUp = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_up)
        val rotateDown = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_down)

        menageIcons()

        binding.include1.btnDashboardHeader.setOnClickListener {
            safeNavController?.safeNavigate(AddOrEditMarimoFragmentDirections
                .actionAddOrEditFragmentToDashboardFragment())
        }

        val frequencies = listOf(
            "Every 7 days (Weekly)",
            "Every 10 days",
            "Every 14 days (Bi-weekly)",
            "Every 21 days",
            "Every 30 days (Monthly)"
        )

        val adapter = CustomSpinnerAdapter(requireContext(), frequencies)
        binding.marimoSpinnerLayout.customSpinner.adapter = adapter

        binding.marimoSpinnerLayout.customSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                adapter.setSelectedIndex(position)
                binding.marimoSpinnerLayout.arrow.startAnimation(rotateDown)
                isSpinnerOpen = false
                freq = AppUtils.extractDay(frequencies[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.marimoSpinnerLayout.arrow.startAnimation(rotateDown)
                isSpinnerOpen = false
            }
        }

        binding.marimoSpinnerLayout.customSpinner.setPopupBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.spinner_dropdown_background)
        )
        binding.marimoSpinnerLayout.customSpinner.adapter = adapter

        binding.marimoSpinnerLayout.customSpinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!isSpinnerOpen) {
                    binding.marimoSpinnerLayout.arrow.startAnimation(rotateUp)
                    isSpinnerOpen = true
                }
            }
            false
        }

        binding.inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val mDatePicker = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "yyyy-MM-dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat)
                    val data = sdf.format(calendar.time)
                    binding.inputDate.setText(data)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            mDatePicker.datePicker.maxDate = AppUtils.getMaxDate()
            mDatePicker.setTitle("")
            mDatePicker.show()

        }

        binding.btnAdd.setOnClickListener {
            marimoName = binding.inputName.text.toString()
            lastWaterChange = binding.inputDate.text.toString()
            notes = binding.inputNotes.text.toString()

            if (marimoName.isEmpty() || lastWaterChange.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.please_fill_all_the_fields), Toast.LENGTH_SHORT).show()
            } else {
                saveMarimo(marimoName, lastWaterChange, notes, freq)
                clearAll()
            }

        }

        binding.btnCancel.setOnClickListener {
            clearAll()
        }
    }

    private fun clearAll() {
        binding.inputName.text.clear()
        binding.inputDate.text.clear()
        binding.inputNotes.text.clear()
        binding.marimoSpinnerLayout.customSpinner.setSelection(0)
    }

    private fun saveMarimo(marimoName: String, lastWaterChange: String, notes: String, freq: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            RepositoryManager.marimoRepository.addMarimo(marimoName, lastWaterChange, notes, freq)
        }
    }

    private fun menageIcons() {

        binding.include1.btnAddMarimoHeader.background = ContextCompat.getDrawable(
            binding.root.context, R.drawable.bg_button_light_green)
        binding.include1.btnAddMarimoHeader.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add, 0, 0);
        DrawableCompat.setTint(binding.include1.btnAddMarimoHeader.compoundDrawables[1], resources.getColor(R.color.marimo_item_green))


        binding.include1.btnDashboardHeader.background = ContextCompat.getDrawable(
            binding.root.context, R.drawable.bg_button_white)
        binding.include1.btnDashboardHeader.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dashboard, 0, 0);
        DrawableCompat.setTint(binding.include1.btnDashboardHeader.compoundDrawables[1], resources.getColor(R.color.marimo_add_icon))

    }
}
package rpt.tool.marimocare.ui.marimo

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentAddOrEditBinding
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate


class AddOrEditMarimoFragment:
    BaseFragment<FragmentAddOrEditBinding>(FragmentAddOrEditBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            frequencies
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.inputFrequency.adapter = adapter
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

/*android:id="@+id/btnDashboardHeader"
android:layout_width="96dp"
android:layout_height="42dp"
android:drawableTop="@drawable/ic_dashboard"
android:drawableTint="@color/marimo_item_green"
android:textAllCaps="false"
android:textColor="@color/marimo_item_green"
app:backgroundTint="@null"
android:background="@drawable/bg_button_light_green"
android:paddingLeft="16dp"
android:paddingRight="16dp"
tools:ignore="ButtonStyle,RtlHardcoded" />*/
package rpt.tool.marimocare.ui.marimo

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentUpdateMarimoFromWidgetBinding
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.MarimoUpdate
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.view.adapters.MarimoUpdateAdapter

class UpdateMarimoFromWidgetFragment : BaseFragment<FragmentUpdateMarimoFromWidgetBinding>(
    FragmentUpdateMarimoFromWidgetBinding::inflate) {

    private lateinit var adapter: MarimoUpdateAdapter
    private var marimoToUpdate: MutableList<MarimoUpdate> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        fun updateButton() {
            val count = marimoToUpdate.count { it.selected }
            binding.btnUpdate.text = buildString {
                append(getString(R.string.update_dialog))
                append(" ")
                append(count)
                append(" ")
                append("Marimo")
            }
        }

        adapter = MarimoUpdateAdapter(marimoToUpdate) {
            updateButton()
        }

        binding.recyclerMarimos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMarimos.adapter = adapter

        updateButton()

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnUpdate.setOnClickListener {
            val list = marimoToUpdate.filter { it.selected }
            updateMarimos(list)
            findNavController().popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMarimos(list: List<MarimoUpdate>) {

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            list.forEach {
                val marimo = RepositoryManager.marimoRepository.getMarimo(it.id)
                if (marimo != null) {
                    val lastChanged = AppUtils.getCurrentDate()
                    RepositoryManager.marimoRepository.updateWaterMarimo(lastChanged, it.id)
                }
            }

            AlertDataUtils.recalc(requireContext())
        }
    }
}
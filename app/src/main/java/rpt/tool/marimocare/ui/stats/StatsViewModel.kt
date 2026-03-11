package rpt.tool.marimocare.ui.stats


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoDetailUi
import rpt.tool.marimocare.utils.managers.RepositoryManager

class StatsViewModel : ViewModel() {

    val allMarimos: LiveData<List<Marimo>> = RepositoryManager.marimoRepository.marimos

    private val _comparisonDetails = MutableStateFlow<List<MarimoDetailUi>>(emptyList())
    val comparisonDetails: StateFlow<List<MarimoDetailUi>> = _comparisonDetails.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDetailsForSelection(selectedMarimos: List<Marimo>) {
        if (selectedMarimos.isEmpty()) {
            _comparisonDetails.value = emptyList()
            return
        }

        viewModelScope.launch {
            val uiModels = withContext(Dispatchers.IO) {
                selectedMarimos.map { marimo ->

                    val latestHealth = RepositoryManager.marimoRepository.getSpecificHealth(
                        marimo.code, null
                    )

                    val healthString = latestHealth.let { "${it}/100" } ?: "N/A"

                    val changesCount = RepositoryManager.marimoRepository
                        .getMarimoTotalWaterChanged(marimo.code)

                    val daysTracked = calculateDaysFromRegistration(
                        marimo.registrationDate)

                    MarimoDetailUi(
                        marimoCode = marimo.code,
                        name = marimo.name,
                        healthValue = latestHealth,
                        healthScoreString = healthString,
                        totalChanges = changesCount.toString(),
                        frequencyDays = marimo.changeFrequencyDays.toString(),
                        daysTracked = daysTracked.toString()
                    )
                }
            }

            _comparisonDetails.value = uiModels
        }
    }

    private fun calculateDaysFromRegistration(dateString: String?): Long {
        if (dateString.isNullOrEmpty()) return 0
        return try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault())
            val regDate = format.parse(dateString)
            if (regDate != null) {
                val diffInMillis = System.currentTimeMillis() - regDate.time
                java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffInMillis)
            } else 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun resetComparison() {
        _comparisonDetails.value = emptyList()
    }
}
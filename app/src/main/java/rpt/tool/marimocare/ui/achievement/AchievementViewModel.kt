package rpt.tool.marimocare.ui.achievement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.utils.data.appmodels.Achievement
import rpt.tool.marimocare.utils.managers.RepositoryManager

class AchievementViewModel : ViewModel() {

    private val _earnedAchievements = MutableLiveData<List<Achievement>>()
    val earnedAchievements: LiveData<List<Achievement>> = _earnedAchievements

    private val _lockedAchievements = MutableLiveData<List<Achievement>>()
    val lockedAchievements: LiveData<List<Achievement>> = _lockedAchievements

    fun loadAchievements() {
        viewModelScope.launch(Dispatchers.IO) {
            val earned = RepositoryManager.marimoRepository.getEarnedAchievements()
            val locked = RepositoryManager.marimoRepository.getLockedAchievements()

            withContext(Dispatchers.Main) {
                _earnedAchievements.value = earned
                _lockedAchievements.value = locked
            }
        }
    }
}

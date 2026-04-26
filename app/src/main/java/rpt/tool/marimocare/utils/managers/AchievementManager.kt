package rpt.tool.marimocare.utils.managers

class AchievementManager {
    companion object {
        fun recalculateAll() {
            TODO("Not yet implemented")
        }

        fun deleteAllAchievement() {
            RepositoryManager.marimoRepository.resetAllAchievements()
        }

        fun earnAchievement(id:Int, date: String) {
            RepositoryManager.marimoRepository.earnAchievement(id, date)
        }
    }
}
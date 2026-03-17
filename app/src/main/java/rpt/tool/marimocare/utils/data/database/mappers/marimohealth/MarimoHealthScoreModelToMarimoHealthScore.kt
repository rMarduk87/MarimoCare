package rpt.tool.marimocare.utils.data.database.mappers.marimohealth

import android.os.Build
import androidx.annotation.RequiresApi
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScore
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.MarimoHealthScoreModel

class MarimoHealthScoreModelToMarimoHealthScore : ModelMapper<MarimoHealthScoreModel, MarimoHealthScore> {
    override val destination: Class<MarimoHealthScore> = MarimoHealthScore::class.java

    @RequiresApi(Build.VERSION_CODES.O)
    override fun map(source: MarimoHealthScoreModel): MarimoHealthScore {
        return MarimoHealthScore(
            code = source.code,
            marimoCode = source.marimoCode,
            date = source.date,
            health = source.health,
        )
    }
}
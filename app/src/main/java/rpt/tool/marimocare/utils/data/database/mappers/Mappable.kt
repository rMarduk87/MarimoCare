package rpt.tool.marimocare.utils.data.database.mappers

import androidx.room.Ignore
import rpt.tool.marimocare.utils.data.*

@Suppress("UNCHECKED_CAST")
abstract class Mappable {
    @Ignore
    open var mappers: ArrayList<ModelMapper<Mappable, *>> = arrayListOf()

    @Ignore
    inline fun <reified T> map(): T {
        return mappers.singleOrNull { it.destination == T::class.java }?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }

    open fun <T : AppModel> toAppModel(): T {
        return mappers.singleOrNull { it.destination.superclass == AppModel::class.java }
            ?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }

    open fun <T : DbModel> toDBModel(): T {
        return mappers.singleOrNull { it.destination.superclass == DbModel::class.java }
            ?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }
}

@Ignore
fun <T : Mappable> T.addMapper(mapper: ModelMapper<T, *>) {
    this.mappers.add(mapper as ModelMapper<Mappable, *>)
}
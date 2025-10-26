package rpt.tool.marimocare.utils.view.recyclerview

import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.MutableSubItemList
import com.mikepenz.fastadapter.binding.BindingViewHolder
import rpt.tool.marimocare.utils.Inflate

abstract class BaseExpandableRecyclerViewBindingItem<VB : ViewBinding>(inflate: Inflate<VB>) :
    BaseRecyclerViewBindingItem<VB>(inflate), IExpandable<BindingViewHolder<VB>> {

    private val _subItems = MutableSubItemList<ISubItem<*>>(this)
    var constraint: CharSequence? = null
    open val predicate: ((ISubItem<*>, CharSequence) -> Boolean)? = null
    override var isExpanded: Boolean = false
    override val isAutoExpanding: Boolean = true
    override var isSelectable: Boolean
        get() = !(isSelected && !isExpanded)
        set(_) {}
    override var parent: IParentItem<*>? = null
    override var subItems: MutableList<ISubItem<*>>
        set(value) = _subItems.setNewList(value)
        get() = takeUnless { constraint.isNullOrBlank() || predicate == null }?.let {
            _subItems.filter {
                predicate!!.invoke(
                    it,
                    constraint!!
                )
            }.toMutableList()
        } ?: _subItems
}
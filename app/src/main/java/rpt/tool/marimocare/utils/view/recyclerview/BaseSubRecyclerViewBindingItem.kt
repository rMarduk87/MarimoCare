package rpt.tool.marimocare.utils.view.recyclerview

import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import rpt.tool.marimocare.utils.Inflate


abstract class BaseSubRecyclerViewBindingItem<VB : ViewBinding>(inflate: Inflate<VB>) :
    BaseRecyclerViewBindingItem<VB>(inflate), ISubItem<BindingViewHolder<VB>> {
    override var parent: IParentItem<*>? = null
}
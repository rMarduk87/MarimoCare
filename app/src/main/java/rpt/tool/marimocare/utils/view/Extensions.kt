package rpt.tool.marimocare.utils.view

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputEditText
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.EventHook
import rpt.tool.marimocare.utils.constants.emptyString
import kotlin.collections.isNotEmpty
import kotlin.collections.toList
import kotlin.let
import kotlin.sequences.count
import kotlin.sequences.forEach
import kotlin.takeIf
import kotlin.text.toIntOrNull

fun View.enable(enabled: Boolean) {
    this.alpha = if (enabled) {
        1f
    } else {
        0.45f
    }
    this.isEnabled = enabled
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun <T : IItem<*>> RecyclerView.defaultSetUp(
    fastAdapter: FastAdapter<T>,
    vararg hooks: EventHook<T> = arrayOf()
) {
    this.adapter = fastAdapter
    this.layoutManager = LinearLayoutManager(context)
    setHasFixedSize(true)
    hooks.takeIf { it.isNotEmpty() }?.let { fastAdapter.addEventHooks(it.toList()) }
}

inline fun <reified T : ViewBinding> RecyclerView.ViewHolder.getFastAdapterItemViewBinding(): T? {
    return (this as? BindingViewHolder<*>)?.binding as? T
}

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}

fun ViewGroup.clearFocusOnChildren() {
    this.children.takeIf { it.count() > 0 }?.forEach {
        if (it is ViewGroup) it.clearFocusOnChildren()
        it.clearFocus()
    }
}

fun TextInputEditText.updateText(text: String?) {
    val focussed = hasFocus()
    if (focussed) {
        clearFocus()
    }
    setText(text ?: emptyString)
    if (focussed) {
        requestFocus()
    }
}


fun String.isInteger() = this.toIntOrNull()?.let { true } ?: false

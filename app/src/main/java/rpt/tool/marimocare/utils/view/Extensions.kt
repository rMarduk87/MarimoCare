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

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import rpt.tool.marimocare.R
import java.io.File
import android.content.Context
import android.net.Uri
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.io.FileOutputStream

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

fun ImageView.loadMarimoImage(file: File?) {
    this.clearColorFilter()
    this.imageTintList = null
    this.background = null

    if (file != null && file.exists()) {
        Glide.with(this.context)
            .load(file)
            .centerCrop()
            .placeholder(R.drawable.ic_water_drop_white)
            .into(this)
    } else {
        this.setImageResource(R.drawable.ic_water_drop_white)
        this.setColorFilter(ContextCompat.getColor(this.context, R.color.marimo_teal_accent), PorterDuff.Mode.SRC_IN)
    }
}

fun getHealthColorRes(health: Int): Int {
    return when {
        health == 100 -> R.color.health100
        health in 40..70 -> R.color.health4070
        health in 1..19 -> R.color.health119
        health <= 0 -> R.color.health_red
        else -> R.color.health_normal
    }
}

fun Int.getHealthColor(context: Context): Int = ContextCompat.getColor(context, getHealthColorRes(this))

fun getHealthTextColorRes(health: Int): Int {
    return if (health in 40..70) R.color.marimo_dark
    else android.R.color.white
}

fun View.applyHealthColor(health: Int) {
    this.backgroundTintList = ColorStateList.valueOf(health.getHealthColor(this.context))
}

fun MaterialCardView.applyHealthStroke(health: Int) {
    this.strokeColor = health.getHealthColor(this.context)
}

fun TextView.applyHealthTextColor(health: Int) {
    this.setTextColor(ContextCompat.getColor(this.context, getHealthTextColorRes(health)))
}

fun Context.copyUriToInternalFile(uri: Uri): File {
    val file = File(
        this.filesDir,
        "marimo_${System.currentTimeMillis()}.jpg"
    )

    this.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}

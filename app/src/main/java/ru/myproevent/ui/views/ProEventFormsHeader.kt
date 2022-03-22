package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.ProeventFormsHeaderBinding

class ProEventFormsHeader : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(binding.root)
    }

    var title: String = ""
        set(value) {
            binding.bar.text = value
            field = value
        }

    var isExpanded: Boolean = false
        set(value) {
            if (value == field) {
                return
            }
            if (value) {
                open()
            } else {
                close()
            }
            field = value
        }

    fun setEditIcon(editIcon: Int?) = with(binding){
        editIcon?.let {
            editItems.visibility = View.VISIBLE
            editItems.setImageResource(it)
        } ?: run {
            editItems.visibility = View.GONE
        }
    }

    fun setEditIconTint(editIconTint: Int?) = with(binding){
        if (editIconTint == null) {
            editItems.clearColorFilter()
        } else {
            editItems.setColorFilter(
                ContextCompat.getColor(
                    ProEventApp.instance.applicationContext,
                    editIconTint
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    var onExpandClickListener: (() -> Unit)? = null
    var onEditItemsClickListener: (() -> Unit)? = null

    private val binding = ProeventFormsHeaderBinding.inflate(LayoutInflater.from(context)).apply {
        bar.setOnClickListener { expand.performClick() }
        expand.setOnClickListener { onExpandClickListener?.invoke() }
        editItems.setOnClickListener {  onEditItemsClickListener?.invoke() }
    }

    private fun open() = with(binding.expand){
        setColorFilter(
            ContextCompat.getColor(
                ProEventApp.instance.applicationContext,
                R.color.ProEvent_bright_orange_300
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun close() = with(binding.expand){
        setColorFilter(
            ContextCompat.getColor(
                ProEventApp.instance.applicationContext,
                R.color.ProEvent_blue_800
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
    }
}
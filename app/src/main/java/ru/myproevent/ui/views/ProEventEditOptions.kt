package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.ItemEditOptionsBinding

class ProEventEditOptions : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(binding.root)
        initViews()
    }

    val editableForms = mutableListOf<ProEventEditableForm>()

    private val binding = ItemEditOptionsBinding.inflate(LayoutInflater.from(context))

    private fun initViews() = with(binding) {
        cancel.setOnClickListener {
            for (form in editableForms) {
                form.cancelEdit()
            }
        }
        save.setOnClickListener {
            for (form in editableForms) {
                form.saveEdit()
            }
        }
    }

    interface ProEventEditableForm {
        fun saveEdit()
        fun cancelEdit()
    }
}
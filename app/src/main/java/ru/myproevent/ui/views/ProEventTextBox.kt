package ru.myproevent.ui.views

import android.content.Context
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import ru.myproevent.databinding.ViewTextBoxBinding

class ProEventTextBox : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        lockEdit()
        addView(binding.root)
    }

    private val binding = ViewTextBoxBinding.inflate(LayoutInflater.from(context)).apply {
        textBox.doAfterTextChanged { value ->
            valueUpdateCallback?.invoke(value.toString())
        }
    }

    var value: String = ""
        set(value) {
            binding.textBox.setText(value)
            field = value
        }
        get() {
            return binding.textBox.text.toString()
        }

    var isEditLocked: Boolean = true
        set(value) {
            if(value == field){
                return
            }
            if (value) {
                lockEdit()
            } else {
                unlockEdit()
            }
            field = value
        }

    // private lateinit var defaultKeyListener: KeyListener

    var valueUpdateCallback: ((value: String) -> Unit)? = null

    private fun lockEdit() = with(binding){
        textBox.clearFocus()
        textBox.hideKeyBoard()
        // defaultKeyListener = textBox.keyListener
        //textBox.keyListener = null
        textBox.isEnabled = false
    }

    private fun unlockEdit() = with(binding){
        //textBox.keyListener = defaultKeyListener
        textBox.isEnabled = true
        textBox.text?.let { textBox.setSelection(it.length) }
    }
}
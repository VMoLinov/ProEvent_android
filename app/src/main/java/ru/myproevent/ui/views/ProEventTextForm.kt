package ru.myproevent.ui.views

import android.content.Context
import android.text.method.KeyListener
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.ProeventTextFormBinding

class ProEventTextForm : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        showEditOption()
        Log.d("[MYLOG]", "showEditOption call from constructor")
        lockEdit()
        addView(binding.root)
    }

    private val binding = ProeventTextFormBinding.inflate(LayoutInflater.from(context)).apply {
        inputEdit.doAfterTextChanged { value ->
            valueUpdateCallback?.invoke(value.toString())
        }
    }

    var title: String = ""
        set(value) {
            binding.inputTitle.text = value
            field = value
        }

    var hint: String = ""
        set(value) {
            binding.inputEdit.hint = value
            field = value
        }

    var value: String = ""
        set(value) {
            binding.inputEdit.setText(value)
            field = value
        }
        get() {
            return binding.inputEdit.text.toString()
        }

    var isEditLocked: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            if (value) {
                lockEdit()
            } else {
                unlockEdit()
            }
            field = value
        }

    var isEditOptionVisible: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            if (value) {
                showEditOption()
                Log.d("[MYLOG]", "showEditOption call from isEditOptionVisible setter")
            } else {
                hideEditOption()
            }
            field = value
        }

    // private lateinit var defaultKeyListener: KeyListener

    var editUnlockCallback: (() -> Unit)? = null
    var editOptionHideCallback: (() -> Unit)? = null
    var valueUpdateCallback: ((value: String) -> Unit)? = null

    private fun showEditOption() {
        Log.d("[MYLOG]", "showEditOption")
        binding.input.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.input.endIconDrawable =
            AppCompatResources.getDrawable(
                ProEventApp.instance.applicationContext,
                R.drawable.ic_edit
            )!!
        binding.input.setEndIconOnClickListener {
            isEditLocked = false
            isEditOptionVisible = false
            Log.d("[MYLOG]", "isEditOptionVisible set from setEndIconOnClickListener")
            editUnlockCallback?.invoke()
            binding.inputEdit.requestFocus()
            showKeyBoard(binding.inputEdit)
        }
    }

    private lateinit var defaultKeyListener: KeyListener

    private fun hideEditOption() {
        binding.input.endIconMode = TextInputLayout.END_ICON_NONE
        editOptionHideCallback?.invoke()
    }

    private fun lockEdit() {
        binding.inputEdit.clearFocus()
        binding.inputEdit.hideKeyBoard()
        defaultKeyListener = binding.inputEdit.keyListener
        binding.inputEdit.keyListener = null
        // binding.inputEdit.isEnabled = false
    }

    private fun unlockEdit() {
        binding.inputEdit.keyListener = defaultKeyListener
        // binding.inputEdit.isEnabled = true
        binding.inputEdit.text?.let { it1 -> binding.inputEdit.setSelection(it1.length) }
        //showEditOptions()
    }

    private fun showKeyBoard(view: View) {
        val imm: InputMethodManager =
            ProEventApp.instance.applicationContext.getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}
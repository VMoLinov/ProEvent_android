package ru.myproevent.ui.views

import android.content.Context
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputLayout
import ru.myproevent.R

open class TextInputLayoutEditTool : TextInputLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var defaultKeyListener: KeyListener? = null
    protected var textEdit: KeyboardAwareTextInputEditText? = null
    protected var actOnClick: ((id: Int) -> Unit)? = null

    fun setEditListeners(textEdit: KeyboardAwareTextInputEditText, actOnClick: (id: Int) -> Unit) {
        this.actOnClick = actOnClick
        this.textEdit = textEdit
        endIconMode = END_ICON_CUSTOM
        endIconDrawable = context.getDrawable(R.drawable.ic_edit)
        textEdit.isFocusableInTouchMode = false
        defaultKeyListener = textEdit.keyListener
        textEdit.keyListener = null
        setEndIconOnClickListener { actOnClick(id) }
    }

    protected open fun setRedacting() {
        textEdit?.let { textEdit ->
            textEdit.isFocusableInTouchMode = true
            textEdit.keyListener = defaultKeyListener
            textEdit.requestFocus()
            showKeyBoard(textEdit)
            textEdit.text?.let { it1 -> textEdit.setSelection(it1.length) }
            endIconMode = END_ICON_NONE
        }

    }


    private fun showKeyBoard(view: View) {
        val imm: InputMethodManager =
            context.getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun setRedacting(isEdited: Boolean): TextInputLayoutEditTool {
        if (isEdited) {
            if (textEdit?.keyListener == null)
                setRedacting()
        } else if (textEdit?.keyListener != null)
            setEditListeners(textEdit!!, actOnClick!!)
        return this
    }
}
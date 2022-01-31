package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText


// В отличии от TextInputEditText теряет фокус когда клавиатура скрыта
class KeyboardAwareAutoCompleteTextView : AppCompatAutoCompleteTextView {
    var selectionChangedListener: ((selStart: Int, selEnd: Int) -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onEditorAction(actionCode: Int) {
        super.onEditorAction(actionCode)
        if (actionCode == EditorInfo.IME_ACTION_DONE) {
            clearFocus()
            // Почему то, super.onEditorAction(actionCode) не прячет клавиатуру,
            // если после его вызова выполняется clearFocus(),
            // поэтому здесь клавиатура прячестся повторно
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                if (isActive(this@KeyboardAwareAutoCompleteTextView)) {
                    hideSoftInputFromWindow(windowToken, 0)
                }
            }
        }
    }

    // TODO: отрефакторить - избавиться от этого метода(сделать его private)
    fun hideKeyBoard(){
        clearFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            if (isActive(this@KeyboardAwareAutoCompleteTextView)) {
                hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
            return false
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        selectionChangedListener?.let { it(selStart, selEnd) }
    }
}
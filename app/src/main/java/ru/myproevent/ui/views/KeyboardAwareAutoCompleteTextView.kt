package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.jakewharton.rxbinding2.widget.RxTextView
import ru.myproevent.domain.models.Suggestion
import java.util.concurrent.TimeUnit


// В отличии от TextInputEditText теряет фокус когда клавиатура скрыта
class KeyboardAwareAutoCompleteTextView : AppCompatAutoCompleteTextView {
    var selectionChangedListener: ((selStart: Int, selEnd: Int) -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var adapter: ArrayAdapter<String>? = null

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

    fun initHints(searchHints: (String) -> Unit) {
        adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, arrayOf())
        setAdapter(adapter)
        RxTextView.textChangeEvents(this)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe {
                searchHints(it.text().toString())
            }
    }

    fun setEmailHint(emailSuggestion: List<Suggestion>) {
        adapter?.clear()
        adapter?.addAll(emailSuggestion.map { it.value })
        adapter?.filter?.filter("", null);
    }
}
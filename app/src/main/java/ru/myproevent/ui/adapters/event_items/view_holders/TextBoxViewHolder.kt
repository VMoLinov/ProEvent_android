package ru.myproevent.ui.adapters.event_items.view_holders

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.ItemTextBoxBinding
import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.ITextBoxItemView

class TextBoxViewHolder(
    private val binding: ItemTextBoxBinding,
    private val presenter: IItemPresenter<ITextBoxItemView>
) :
    RecyclerView.ViewHolder(binding.root),
    ITextBoxItemView {
    private fun showKeyBoard(view: View) {
        val imm: InputMethodManager =
            ProEventApp.instance.applicationContext.getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    init {
        itemView.setOnClickListener { presenter.onItemClick(this) }
    }

    override var pos = -1

    override fun setValue(value: String) = with(binding) {
        textBox.value = value
    }

    override fun setOnValueChangedListener(callback: ((value: String) -> Unit)?) = with(binding) {
        textBox.valueUpdateCallback = { value -> callback?.invoke(value) }
    }

    override fun setEditLock(isLocked: Boolean) = with(binding) {
        textBox.isEditLocked = isLocked
    }

    override fun requestFocus(): Unit = with(binding){
        textBox.requestFocus()
        // showKeyBoard(textBox) TODO: не работает
    }
}
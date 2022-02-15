package ru.myproevent.ui.adapters.event_items.view_holders

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.databinding.ItemProeventTextFormBinding
import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventTextFormItemView

class ProeventTextFormViewHolder(
    private val binding: ItemProeventTextFormBinding,
    private val presenter: IItemPresenter<IProeventTextFormItemView>
) :
    RecyclerView.ViewHolder(binding.root),
    IProeventTextFormItemView {

    init {
        itemView.setOnClickListener { presenter.onItemClick(this) }
    }

    override var pos = -1

    override fun setTitle(title: String) = with(binding) {
        form.title = title
    }

    override fun setHint(hint: String) = with(binding) {
        form.hint = hint
    }

    override fun setValue(value: String) = with(binding) {
        form.value = value
    }

    override fun setEditLock(isLocked: Boolean) = with(binding) {
        form.isEditLocked = isLocked
    }

    override fun setEditOption(isAvailable: Boolean): Unit = with(binding){
        form.isEditOptionVisible = isAvailable
        Log.d("[MYLOG]", "isEditOptionVisible set from setEditOption isAvailable: $isAvailable")
    }

    override fun setOnEditUnlockListener(callback: (() -> Unit)?) = with(binding) {
        form.editUnlockCallback = callback
    }

    override fun setOnEditOptionHideListener(callback: (() -> Unit)?) = with(binding){
        form.editOptionHideCallback = callback
    }

    override fun setOnValueChangedListener(callback: ((value: String) -> Unit)?) =
        with(binding) {
            form.valueUpdateCallback = callback
        }
}
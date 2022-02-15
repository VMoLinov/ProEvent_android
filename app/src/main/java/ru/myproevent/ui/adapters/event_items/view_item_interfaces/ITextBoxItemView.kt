package ru.myproevent.ui.adapters.event_items.view_item_interfaces

import ru.myproevent.ui.presenters.IItemView

interface ITextBoxItemView: IItemView {
    fun setValue(value: String)
    // TODO: использовать StringBuilder, а не String
    fun setOnValueChangedListener(callback: ((value: String) -> Unit)?)
    fun setEditLock(isLocked: Boolean)
    fun requestFocus()
}
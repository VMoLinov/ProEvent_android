package ru.myproevent.ui.adapters.event_items.view_item_interfaces

import ru.myproevent.ui.presenters.IItemView

interface IProeventTextFormItemView: IItemView {
    fun setTitle(title: String)
    fun setHint(hint: String)
    fun setValue(value: String)
    fun setEditLock(isLocked: Boolean)
    fun setEditOption(isAvailable: Boolean)
    fun setOnEditUnlockListener(callback: (() -> Unit)?)
    fun setOnEditOptionHideListener(callback: (() -> Unit)?)
    // TODO: использовать StringBuilder, а не String
    fun setOnValueChangedListener(callback: ((value: String) -> Unit)?)
}
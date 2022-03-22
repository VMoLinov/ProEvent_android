package ru.myproevent.ui.adapters.event_items.view_item_interfaces

import ru.myproevent.ui.adapters.event_items.EVENT_SCREEN_ITEM_ID
import ru.myproevent.ui.presenters.IItemView

interface IProeventFormsHeaderItemView: IItemView {
    var itemId: EVENT_SCREEN_ITEM_ID
    fun setTitle(title: String)
    fun setExpandState(isExpanded: Boolean)
    fun setEditOptionIcon(editIcon: Int?)
}
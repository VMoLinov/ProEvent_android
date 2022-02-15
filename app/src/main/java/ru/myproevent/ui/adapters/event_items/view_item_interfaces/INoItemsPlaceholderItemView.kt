package ru.myproevent.ui.adapters.event_items.view_item_interfaces

import ru.myproevent.ui.presenters.IItemView

interface INoItemsPlaceholderItemView: IItemView {
    fun setDescription(text: String, spanImageRes: Int, spanImagePos: Int)
}
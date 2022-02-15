package ru.myproevent.ui.adapters.event_items.view_item_interfaces

import ru.myproevent.ui.presenters.IItemView

interface IEventDateItemView: IItemView {
    fun setStartDate(timestamp: Long)
    fun setEndDate(timestamp: Long)
}
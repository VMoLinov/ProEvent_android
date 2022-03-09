package ru.myproevent.ui.adapters.event_items.presenter_interfaces

import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IEventDateItemView

interface IEventDateItemPresenter: IItemPresenter<IEventDateItemView> {
    fun onEditClick(view: IEventDateItemView)
    fun onRemoveClick(view: IEventDateItemView)
}
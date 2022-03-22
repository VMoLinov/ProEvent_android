package ru.myproevent.ui.adapters.event_items.presenter_interfaces

import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IEventDateItemView
import ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.ItemPresenter

abstract class IEventDateItemPresenter: ItemPresenter<IEventDateItemView>() {
    abstract fun onEditClick(view: IEventDateItemView)
    abstract fun onRemoveClick(view: IEventDateItemView)
}
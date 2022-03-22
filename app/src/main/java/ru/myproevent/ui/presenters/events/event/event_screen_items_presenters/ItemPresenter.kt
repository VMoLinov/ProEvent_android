package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters

import ru.myproevent.ui.adapters.IItemPresenter

abstract class ItemPresenter<V>: IItemPresenter<V> {
    override fun onItemClick(view: V) {}
    override fun bindView(view: V) {}
}
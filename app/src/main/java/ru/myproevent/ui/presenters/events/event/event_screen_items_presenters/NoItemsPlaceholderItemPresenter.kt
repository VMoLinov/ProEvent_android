package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters

import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.INoItemsPlaceholderItemView
import ru.myproevent.ui.presenters.events.event.EventPresenter

class NoItemsPlaceholderItemPresenter(val eventPresenter: EventPresenter) :
    ItemPresenter<INoItemsPlaceholderItemView>() {
    override fun bindView(view: INoItemsPlaceholderItemView) =
        with(eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.NoItemsPlaceholder) {
            view.setDescription(description, spanImageRes, spanImagePos)
        }
}
package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters

import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.data_items.no_items_placeholders.NoItemsPlaceholderFactory
import ru.myproevent.ui.presenters.events.event.EventPresenter

class PointsHeaderPresenter(val item: EventScreenItem.FormsHeader<EventScreenItem.ListItem>, val eventPresenter: EventPresenter): IHeaderPresenter {
    init {
        item.items.add(NoItemsPlaceholderFactory.create(header = item))
    }

    override fun onEditOptionClick(headerPosition: Int) {
        eventPresenter.viewState.showMessage("Данный функционал пока не реализован, так как для него нет законченного дизайна")
    }
}
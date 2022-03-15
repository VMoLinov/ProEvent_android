package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters

import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.data_items.no_items_placeholders.NoDatesPlaceholderFactory
import ru.myproevent.ui.presenters.events.event.EventPresenter

class DatesHeaderPresenter(val item: EventScreenItem.FormsHeader<EventScreenItem.ListItem>, val eventPresenter: EventPresenter): IHeaderPresenter {
    init {
        if (!eventPresenter.eventBeforeEdit?.dates.isNullOrEmpty()) {
            eventPresenter.eventBeforeEdit!!.dates.forEach { timeInterval ->
                item.items.add(EventScreenItem.EventDateItem(timeInterval, item))
            }
        } else {
            item.items.add(
                NoDatesPlaceholderFactory.create(header = item)
            )
        }
    }

    override fun onEditOptionClick(headerPosition: Int) {
        eventPresenter.openDatePicker(null)
    }
}
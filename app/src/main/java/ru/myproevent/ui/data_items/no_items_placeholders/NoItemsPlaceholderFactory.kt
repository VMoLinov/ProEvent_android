package ru.myproevent.ui.data_items.no_items_placeholders

import ru.myproevent.R
import ru.myproevent.ui.adapters.event_items.EventScreenItem

class NoItemsPlaceholderFactory {
    companion object {
        fun create(header: EventScreenItem.FormsHeader<EventScreenItem.ListItem>) =
            EventScreenItem.NoItemsPlaceholder(
                "Отсутствуют.\nНажмите + чтобы добавить.",
                R.drawable.ic_add,
                21,
                header
            )
    }
}
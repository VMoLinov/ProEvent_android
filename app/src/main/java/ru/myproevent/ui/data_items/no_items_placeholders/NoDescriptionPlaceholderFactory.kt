package ru.myproevent.ui.data_items.no_items_placeholders

import ru.myproevent.R
import ru.myproevent.ui.adapters.event_items.EventScreenItem

class NoDescriptionPlaceholderFactory {
    companion object {
        fun create(header: EventScreenItem.FormsHeader<EventScreenItem.ListItem>) =
            EventScreenItem.NoItemsPlaceholder(
                "Отсутствует.\nНажмите + чтобы добавить.",
                R.drawable.ic_edit_blue,
                21,
                header
            )
    }
}
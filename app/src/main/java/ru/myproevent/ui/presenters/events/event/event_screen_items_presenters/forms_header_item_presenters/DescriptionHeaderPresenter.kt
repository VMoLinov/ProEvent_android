package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters

import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.data_items.no_items_placeholders.NoDescriptionPlaceholderFactory
import ru.myproevent.ui.presenters.events.event.EventPresenter

// TODO: отрефатокрить: переименовть, это часть eventPresenter-a, а не самостоятельный presenter
class DescriptionHeaderPresenter(
    val item: EventScreenItem.FormsHeader<EventScreenItem.ListItem>,
    val eventPresenter: EventPresenter
) : IHeaderPresenter {
    init {
        item.items.add(
            if (eventPresenter.eventBeforeEdit != null && !eventPresenter.eventBeforeEdit!!.description.isNullOrBlank()) {
                EventScreenItem.TextBox(
                    value = eventPresenter.eventBeforeEdit!!.description!!,
                    header = item,
                    isEditLocked = true,
                    hasFocusIntent = false
                )
            } else {
                NoDescriptionPlaceholderFactory.create(header = item)
            }
        )
    }

    override fun onEditOptionClick(headerPosition: Int) {
        item.editOptionIcon = null
        enableDescriptionEdit(headerPosition)
        eventPresenter.eventScreenListPresenter.formsHeaderItemPresenter.absoluteFormsHeaderPresenter.updateAbsoluteFormsHeader()
        eventPresenter.viewState.showEditOptions()
    }

    private fun enableDescriptionEdit(headerPosition: Int) {
        with(eventPresenter.eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
            if (items.first() is EventScreenItem.NoItemsPlaceholder) {
                removeHeaderItemsFromScreenItems(headerPosition)
                eventPresenter.eventScreenListPresenter.eventScreenItems.addAll(headerPosition + 1, items.apply {
                    clear()
                    add(
                        EventScreenItem.TextBox(
                            value = "",
                            header = this@with,
                            isEditLocked = false,
                            hasFocusIntent = true
                        )
                    )
                })
                eventPresenter.viewState.updateEventScreenList()
            } else {
                if (!isExpanded) {
                    isExpanded = true
                    eventPresenter.eventScreenListPresenter.eventScreenItems.addAll(headerPosition + 1, items)
                }
                with(items.first() as EventScreenItem.TextBox) {
                    isEditLocked = false
                    hasFocusIntent = true
                }
                eventPresenter.viewState.updateEventScreenList()
            }
        }
    }

    private fun removeHeaderItemsFromScreenItems(headerPosition: Int) {
        eventPresenter.eventScreenListPresenter.eventScreenItems.subList(
            headerPosition + 1,
            headerPosition + 1 + (eventPresenter.eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>).items.size
        ).clear()
    }
}
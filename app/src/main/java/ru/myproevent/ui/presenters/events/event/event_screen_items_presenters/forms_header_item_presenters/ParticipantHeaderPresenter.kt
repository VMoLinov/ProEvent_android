package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters

import ru.myproevent.domain.utils.toParticipantItems
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.data_items.no_items_placeholders.NoItemsPlaceholderFactory
import ru.myproevent.ui.presenters.events.event.EventPresenter

class ParticipantHeaderPresenter(val item: EventScreenItem.FormsHeader<EventScreenItem.ListItem>, val eventPresenter: EventPresenter): IHeaderPresenter {
    init {
        val participantItems =
            eventPresenter. eventBeforeEdit?.participantsUserIds?.toParticipantItems(item)

        if (!participantItems.isNullOrEmpty()) {
            participantItems.forEach {
                item.items.add(it)
            }
        } else {
            item.items.add(
                NoItemsPlaceholderFactory.create(header = item)
            )
        }
    }

    override fun onEditOptionClick(headerPosition: Int) {
        eventPresenter.localRouter.navigateTo(
            eventPresenter.screens.participantPickerTypeSelection(
                eventPresenter.pickedParticipantsIds
            )
        )
    }
}
package ru.myproevent.ui.presenters.events.event

import android.util.Log
import ru.myproevent.domain.models.entities.Address
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.adapters.event_items.EVENT_SCREEN_ITEM_ID
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import java.util.*


class EventEditOptionsPresenter(private val eventPresenter: EventPresenter) {
    private var isSaveAvailable = true

    private fun saveCallback(successEvent: Event?) {
        isSaveAvailable = true
        eventPresenter.viewState.enableSaveOptions()
        if (successEvent == null) {
            return
        }
        eventPresenter.eventBeforeEdit = successEvent
        eventPresenter.viewState.hideEditOptions()
        eventPresenter.viewState.eventBarTitleSet(successEvent.name)
        cancelEdit()
    }

    fun saveEvent() {
        // TODO: вынести в кастомную вьюшку
        if (!isSaveAvailable) {
            return
        }
        isSaveAvailable = false
        eventPresenter.viewState.disableSaveOptions()

        val description =
            with(eventPresenter.eventScreenListPresenter.eventScreenItems.find { item -> item.itemId == EVENT_SCREEN_ITEM_ID.DESCRIPTION_HEADER } as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
                if (items.first() is EventScreenItem.TextBox) {
                    (items.first() as EventScreenItem.TextBox).value
                } else {
                    ""
                }
            }

        // TODO: отрефакторить: вынести в extension функцию
        val dates =
            with(eventPresenter.eventScreenListPresenter.eventScreenItems.find { item -> item.itemId == EVENT_SCREEN_ITEM_ID.DATES_HEADER } as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
                if (items.first() is EventScreenItem.NoItemsPlaceholder) {
                    TreeSet()
                } else {
                    TreeSet<TimeInterval>().apply {
                        items.forEach {
                            add((it as EventScreenItem.EventDateItem).timeInterval)
                        }
                    }
                }
            }

        if (eventPresenter.eventBeforeEdit == null) {
            eventPresenter.addEvent(
                name = (eventPresenter.eventScreenListPresenter.eventScreenItems[1] as EventScreenItem.TextForm).value,
                dates = dates,
                address = Address(
                    0.0,
                    0.0,
                    (eventPresenter.eventScreenListPresenter.eventScreenItems[2] as EventScreenItem.TextForm).value
                ),
                description = description,
                uuid = null,
                callback = ::saveCallback
            )
        } else {
            val participantsItems =
                (eventPresenter.eventScreenListPresenter.eventScreenItems.find { item -> item.itemId == EVENT_SCREEN_ITEM_ID.PARTICIPANTS_HEADER } as EventScreenItem.FormsHeader<EventScreenItem.ListItem>).items
            val participantsItemsIterator = participantsItems.iterator()
            val participantsUserIds =
                if (participantsItems.first() is EventScreenItem.NoItemsPlaceholder) {
                    null
                } else {
                    LongArray(
                        participantsItems.size
                    ) { return@LongArray (participantsItemsIterator.next() as EventScreenItem.ParticipantItem).participantId }
                }

            val eventAfterEdit = Event(
                id =  eventPresenter.eventBeforeEdit!!.id,
                name = (eventPresenter.eventScreenListPresenter.eventScreenItems[1] as EventScreenItem.TextForm).value,
                ownerUserId = eventPresenter.loginRepository.getLocalId()!!,
                status = Event.Status.ACTUAL,
                description = description,
                dates = dates,
                participantsUserIds = participantsUserIds,
                city = "PLACEHHOLDER",
                address = Address(
                    0.0,
                    0.0,
                    (eventPresenter.eventScreenListPresenter.eventScreenItems[2] as EventScreenItem.TextForm).value
                ),
                mapsFileIds = null,
                pointsPointIds = null,
                imageFile = null
            )
            eventPresenter.editEvent(
                eventAfterEdit,
                callback = ::saveCallback
            )
        }
    }

    fun cancelEdit() {
        eventPresenter.eventBeforeEdit?.let {
            val restoredScreenItems = eventPresenter.getEventScreenItemsBeforeEdit().apply {
                eventPresenter.eventScreenListPresenter.formsHeaderItemPresenter.setOfExpandedItems.forEach {
                    val headerPosition = indexOfFirst { item -> item.itemId == it }
                    (get(headerPosition) as EventScreenItem.FormsHeader<EventScreenItem.ListItem>).isExpanded = true
                    addAll(
                        headerPosition + 1,
                        (get(headerPosition) as EventScreenItem.FormsHeader<EventScreenItem.ListItem>).items
                    )
                }
            }
            eventPresenter.eventScreenListPresenter.eventScreenItems.clear()
            eventPresenter.eventScreenListPresenter.eventScreenItems.addAll(restoredScreenItems)
            // Log.d("[MYLOG]", "eventScreenListPresenter.eventScreenItems: ${eventScreenListPresenter.eventScreenItems}") // TODO: эта строчка ломает kotlin?
            eventPresenter.eventScreenListPresenter.formsHeaderItemPresenter.absoluteFormsHeaderPresenter.updateAbsoluteFormsHeader()
            eventPresenter.viewState.updateEventScreenList()
            eventPresenter.viewState.hideEditOptions()
        } ?: run {
            eventPresenter.localRouter.exit()
        }

        Log.d("[BUG]", "cancelEdit() eventScreenListPresenter.eventScreenItems.size(${eventPresenter.eventScreenListPresenter.eventScreenItems.size})")
    }
}
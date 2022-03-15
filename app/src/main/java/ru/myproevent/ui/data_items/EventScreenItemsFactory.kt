package ru.myproevent.ui.data_items

import ru.myproevent.R
import ru.myproevent.ui.adapters.event_items.EVENT_SCREEN_ITEM_ID
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.presenters.events.event.EventPresenter
import ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters.*
import java.util.*

// TODO: рефакторинг: я не стал делать presenter для каждого item-a, а сделал только для FormsHeader-ов.
//                    Не уверен, что этот класс это хорошая идея, но пока не знаю как лучше сделать
class EventScreenItemsFactory {
    companion object {
        fun create(eventPresenter: EventPresenter) = mutableListOf(
            EventScreenItem.ProfileImageForm(
                itemId = EVENT_SCREEN_ITEM_ID.EVENT_PICTURE,
                text = "Изменить фото мероприятия"
            ),
            EventScreenItem.TextForm(
                itemId = EVENT_SCREEN_ITEM_ID.EVENT_NAME,
                title = "Название",
                hint = "Введите название",
                value = eventPresenter.eventBeforeEdit?.name ?: "",
                isEditLocked = true,
                isEditOptionAvailable = eventPresenter.isCurrentUserOwnsEvent
            ),
            EventScreenItem.TextForm(
                itemId = EVENT_SCREEN_ITEM_ID.LOCATION,
                title = "Место проведения",
                hint = "Введите адрес",
                value = eventPresenter.eventBeforeEdit?.address?.toString() ?: "",
                isEditLocked = true,
                isEditOptionAvailable = eventPresenter.isCurrentUserOwnsEvent
            ),
            EventScreenItem.FormsHeader(
                itemId = EVENT_SCREEN_ITEM_ID.DATES_HEADER,
                title = "Время проведения",
                isExpanded = false,
                items = TreeSet(), // TODO: отрефакторить: как избежать создание этого пустого mutableList? Он нужен просто как загулшка
                editOptionIcon = if (eventPresenter.isCurrentUserOwnsEvent) R.drawable.ic_add else null
            ).apply {
                presenter = DatesHeaderPresenter(
                    this,
                    eventPresenter
                ) // TODO: есть ли способ предать ссылку this прямо в конструкторе?
            },
            EventScreenItem.FormsHeader(
                itemId = EVENT_SCREEN_ITEM_ID.DESCRIPTION_HEADER,
                title = "Описание",
                isExpanded = false,
                items = TreeSet(),
                editOptionIcon = if (eventPresenter.isCurrentUserOwnsEvent) R.drawable.ic_edit_blue else null,
            ).apply {
                presenter = DescriptionHeaderPresenter(this, eventPresenter)
            },
            EventScreenItem.FormsHeader(
                itemId = EVENT_SCREEN_ITEM_ID.MAPS_HEADER,
                title = "Карты меропрития",
                isExpanded = false,
                items = TreeSet(),
                editOptionIcon = if (eventPresenter.isCurrentUserOwnsEvent) R.drawable.ic_add else null
            ).apply {
                presenter = MapsHeaderPresenter(this, eventPresenter)
            },
            EventScreenItem.FormsHeader(
                itemId = EVENT_SCREEN_ITEM_ID.POINTS_HEADER,
                title = "Точки",
                isExpanded = false,
                items = TreeSet(),
                editOptionIcon = if (eventPresenter.isCurrentUserOwnsEvent) R.drawable.ic_add else null
            ).apply {
                presenter = PointsHeaderPresenter(this, eventPresenter)
            },
            EventScreenItem.FormsHeader(
                itemId = EVENT_SCREEN_ITEM_ID.PARTICIPANTS_HEADER,
                title = "Участники",
                isExpanded = false,
                items = TreeSet(),
                editOptionIcon = if (eventPresenter.isCurrentUserOwnsEvent) R.drawable.ic_add else null
            ).apply {
                presenter = ParticipantHeaderPresenter(this, eventPresenter)
            }
        )
    }
}
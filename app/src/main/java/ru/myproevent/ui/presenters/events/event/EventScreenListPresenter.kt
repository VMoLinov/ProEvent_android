package ru.myproevent.ui.presenters.events.event

import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IEventDateItemPresenter
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IEventScreenListPresenter
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IFormsHeaderItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.INoItemsPlaceholderItemView
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventProfilePictureItemView
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IProeventTextFormItemView
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.ITextBoxItemView
import ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.*
import ru.myproevent.ui.presenters.events.event.event_screen_items_presenters.forms_header_item_presenters.FormsHeaderItemPresenter

class EventScreenListPresenter(
    val eventPresenter: EventPresenter,
    val eventScreenItems: MutableList<EventScreenItem> = mutableListOf(),
    override val profilePictureFormItemPresenter: IItemPresenter<IProeventProfilePictureItemView> = ProfilePictureFormItemPresenter(eventPresenter),
    override val textFormItemPresenter: IItemPresenter<IProeventTextFormItemView> = TextFormItemPresenter(eventPresenter),
    override val formsHeaderItemPresenter: IFormsHeaderItemPresenter = FormsHeaderItemPresenter(eventPresenter),
    override val participantItemPresenter: ParticipantItemPresenter = ParticipantItemPresenter(eventPresenter),
    override val eventDateItemPresenter: IEventDateItemPresenter = EventDateItemPresenter(eventPresenter),
    override val noItemsPlaceholderItemPresenter: IItemPresenter<INoItemsPlaceholderItemView> = NoItemsPlaceholderItemPresenter(eventPresenter),
    override val textBoxPresenter: IItemPresenter<ITextBoxItemView> = TextBoxPresenter(eventPresenter)
) : IEventScreenListPresenter {
    override fun getCount() = eventScreenItems.size
    override fun getType(position: Int) = eventScreenItems[position].type.ordinal
}
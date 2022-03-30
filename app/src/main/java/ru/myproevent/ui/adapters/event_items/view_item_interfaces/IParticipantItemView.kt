package ru.myproevent.ui.adapters.event_items.view_item_interfaces

import ru.myproevent.ui.presenters.IItemView

interface IParticipantItemView : IItemView {
    fun setName(name: String)
    fun setDescription(description: String)
    fun setStatus(deleted: Boolean)
}

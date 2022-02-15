package ru.myproevent.ui.presenters.events.event.participant_pickers.participant_from_contacts_picker

import ru.myproevent.ui.presenters.contacts.contacts_list.IContactItemView

interface IContactPickerItemView : IContactItemView {
    fun setSelection(isSelected: Boolean)
}
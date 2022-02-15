package ru.myproevent.ui.presenters.contacts.contacts_list

import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.ui.presenters.IItemView

interface IContactItemView : IItemView {
    fun setName(name: String)
    fun setDescription(description: String)
    fun loadImg(url: String)
    fun setStatus(status: Contact.Status)
}
package ru.myproevent.ui.presenters.contacts.contact

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface ContactView : BaseMvpView {
    fun setTitle(title: String)
    fun setBirthDate(birthDate: String)
    fun setPosition(position: String)
    fun setPhone(phone: String)
}

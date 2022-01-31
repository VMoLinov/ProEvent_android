package ru.myproevent.ui.presenters.contacts.contacts_list

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.domain.models.entities.contact.Action
import ru.myproevent.domain.models.entities.contact.Status
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface ContactsView : BaseMvpView {
    fun init()
    fun hideConfirmationScreen()
    fun showConfirmationScreen(action: Action, callBack: ((confirmed: Boolean) -> Unit)?)
    fun updateContactsList()
    fun setProgressBarVisibility(visible: Boolean)
    fun hideNoContactsLayout()
    fun showNoContactsLayout(status: Status = Status.ALL)
}

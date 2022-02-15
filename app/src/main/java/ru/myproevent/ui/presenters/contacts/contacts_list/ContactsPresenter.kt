package ru.myproevent.ui.presenters.contacts.contacts_list

import com.github.terrakok.cicerone.Router
import ru.myproevent.R
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Contact.Action
import ru.myproevent.domain.models.entities.Contact.Status
import ru.myproevent.domain.models.repositories.contacts.IProEventContactsRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import ru.myproevent.ui.presenters.contacts.contacts_list.adapters.IContactsListPresenter
import javax.inject.Inject

class ContactsPresenter(localRouter: Router) : BaseMvpPresenter<ContactsView>(localRouter) {

    @Inject
    lateinit var contactsRepository: IProEventContactsRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    inner class ContactsListPresenter(
        private var itemClickListener: ((IContactItemView, Contact) -> Unit)? = null,
        private var statusClickListener: ((Contact) -> Unit)? = null
    ) : IContactsListPresenter {

        private val contacts = mutableListOf<Contact>()

        override fun getCount() = contacts.size

        override fun bindView(view: IContactItemView) {
            val pos = view.pos
            fillItemView(view, contacts[pos])
        }

        private fun fillItemView(view: IContactItemView, contact: Contact) {
            contact.apply {
                val name = when {
                    !fullName.isNullOrBlank() -> fullName!!
                    !nickName.isNullOrBlank() -> nickName!!
                    else -> getString(R.string.id_01, id)
                }
                view.setName(name)

                val decs = when {
                    !description.isNullOrBlank() -> description!!
                    else -> getString(R.string.id_01, id)
                }
                view.setDescription(decs)

                status?.let { view.setStatus(it) }
            }
        }

        override fun onItemClick(view: IContactItemView) {
            itemClickListener?.invoke(view, contacts[view.pos])
        }

        override fun onStatusClick(view: IContactItemView) {
            statusClickListener?.invoke(contacts[view.pos])
        }

        fun setData(data: List<Contact>) {
            contacts.clear()
            contacts.addAll(data)
            viewState.updateContactsList()
        }
    }

    val contactsListPresenter = ContactsListPresenter({ _, contact ->
        localRouter.navigateTo(screens.contact(contact.id))
    }, { contact ->
        val action = when (contact.status) {
            Status.DECLINED -> Action.DELETE
            Status.PENDING -> Action.CANCEL
            Status.REQUESTED -> Action.ACCEPT
            else -> return@ContactsListPresenter
        }

        val message = getString(
            when (action) {
                Action.ACCEPT -> R.string.accept_contact_request_question
                Action.CANCEL -> R.string.cancel_request_question
                Action.DECLINE -> R.string.decline_contact_request_question
                Action.DELETE -> R.string.delete_contact_question
                else -> R.string.empty_string
            }
        )

        viewState.showConfirmationScreen(message) { confirmed ->
            viewState.hideConfirmationScreen()
            if (!confirmed) return@showConfirmationScreen
            performActionOnContact(contact, action)
        }
    })

    private fun performActionOnContact(contact: Contact, action: Action) {
        when (action) {
            Action.ACCEPT -> contactsRepository.acceptContact(contact.id)
            Action.CANCEL, Action.DELETE -> contactsRepository.deleteContact(contact.id)
            Action.DECLINE -> contactsRepository.declineContact(contact.id)
            else -> return
        }.observeOn(uiScheduler)
            .subscribe(
                { loadData() },
                { viewState.showMessage(getString(R.string.action_failed)) })
            .disposeOnDestroy()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }

    fun addContact() {
        localRouter.navigateTo(screens.contactAdd())
    }

    fun loadData(status: Status = Status.ALL) {
        viewState.setProgressBarVisibility(true)
        contactsRepository.getContacts(status)
            .observeOn(uiScheduler)
            .subscribe({ data ->
                viewState.setProgressBarVisibility(false)
                contactsListPresenter.setData(data)
                if (data.isEmpty()) viewState.showNoContactsLayout(status)
                else viewState.hideNoContactsLayout()
            }, {
                viewState.setProgressBarVisibility(false)
                viewState.showMessage(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()
    }

    fun filterContacts(status: Status) {
        loadData(status)
    }
}
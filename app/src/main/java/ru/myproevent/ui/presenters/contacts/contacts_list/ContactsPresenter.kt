package ru.myproevent.ui.presenters.contacts.contacts_list

import android.util.Log
import com.github.terrakok.cicerone.Router
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.entities.contact.Action
import ru.myproevent.domain.models.entities.contact.Contact
import ru.myproevent.domain.models.entities.contact.Status
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

        private val contactDTOs = mutableListOf<ContactDto>()

        private var size = 0

        private var contacts = mutableListOf<Contact?>()

        override fun getCount() = size

        override fun bindView(view: IContactItemView) {
            val pos = view.pos

            if (contacts[pos] != null) {
                fillItemView(view, contacts[pos]!!)
            } else {
                val contactDto = contactDTOs[pos]
                profilesRepository.getContact(contactDto)
                    .observeOn(uiScheduler)
                    .subscribe({ contact ->
                        contacts[pos] = contact
                        fillItemView(view, contact)
                    }, {
                        Log.d("[CONTACTS]", "Error: ${it.message}")
                        contacts[pos] = Contact(
                            contactDto.id,
                            fullName = "Заглушка",
                            description = "Профиля нет, или не загрузился",
                            status = Status.fromString(contactDto.status)
                        )
                        fillItemView(view, contacts[pos]!!)
                    }).disposeOnDestroy()
            }
        }

        private fun fillItemView(view: IContactItemView, contact: Contact) {
            contact.apply {
                if (!fullName.isNullOrEmpty()) {
                    view.setName(fullName!!)
                } else if (!nickName.isNullOrEmpty()) {
                    view.setName(nickName!!)
                } else {
                    view.setName(userId.toString())
                }
                if (!description.isNullOrEmpty()) {
                    view.setDescription(description!!)
                } else if (!nickName.isNullOrEmpty()) {
                    view.setDescription(nickName!!)
                } else {
                    view.setDescription("id пользователя: $userId")
                }
                status?.let { view.setStatus(it) }
            }
        }

        override fun onItemClick(view: IContactItemView) {
            contacts[view.pos]?.let { itemClickListener?.invoke(view, it) }
        }

        override fun onStatusClick(view: IContactItemView) {
            contacts[view.pos]?.let { statusClickListener?.invoke(it) }
        }

        fun setData(data: List<ContactDto>, size: Int) {
            this.size = 0
            contactDTOs.clear()
            contactDTOs.addAll(data)
            contacts = MutableList(size) { null }
            this.size = size
            viewState.updateContactsList()
        }
    }

    val contactsListPresenter = ContactsListPresenter({ _, contact ->
        localRouter.navigateTo(screens.contact(contact))
    }, { contact ->
        val action = when (contact.status) {
            Status.DECLINED -> Action.DELETE
            Status.PENDING -> Action.CANCEL
            Status.REQUESTED -> Action.ACCEPT
            else -> return@ContactsListPresenter
        }

        viewState.showConfirmationScreen(action) { confirmed ->
            viewState.hideConfirmationScreen()
            if (!confirmed) return@showConfirmationScreen
            performActionOnContact(contact, action)
        }
    })

    private fun performActionOnContact(contact: Contact, action: Action) {
        when (action) {
            Action.ACCEPT -> contactsRepository.acceptContact(contact.userId)
            Action.CANCEL, Action.DELETE -> contactsRepository.deleteContact(contact.userId)
            Action.DECLINE -> contactsRepository.declineContact(contact.userId)
            else -> return
        }.observeOn(uiScheduler)
            .subscribe({ loadData() }, { viewState.showMessage("Не удалось выполнить действие") })
            .disposeOnDestroy()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
        loadData()
    }

    fun addContact() {
        localRouter.navigateTo(screens.contactAdd())
    }

    fun loadData(status: Status = Status.ALL) {
        viewState.setProgressBarVisibility(true)
        contactsRepository.getContacts(1, Int.MAX_VALUE, status)
            .observeOn(uiScheduler)
            .subscribe({ data ->
                viewState.setProgressBarVisibility(false)
                val size = data.totalElements.toInt()
                contactsListPresenter.setData(data.content, size)
                if (size == 0) viewState.showNoContactsLayout(status)
                    else viewState.hideNoContactsLayout()
            }, {
                viewState.setProgressBarVisibility(false)
                viewState.showMessage("ПРОИЗОШЛА ОШИБКА: ${it.message}")
            }).disposeOnDestroy()
    }

    fun filterContacts(status: Status) {
        loadData(status)
    }
}
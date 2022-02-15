package ru.myproevent.ui.presenters.events.event.participant_pickers.participant_from_contacts_picker

import android.os.Bundle
import android.util.Log
import com.github.terrakok.cicerone.Router
import ru.myproevent.R
import ru.myproevent.domain.models.ContactDto
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.repositories.contacts.IProEventContactsRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.domain.utils.CONTACTS_KEY
import ru.myproevent.domain.utils.PARTICIPANTS_PICKER_RESULT_KEY
import ru.myproevent.domain.utils.toContactDto
import ru.myproevent.ui.presenters.BaseMvpPresenter
import ru.myproevent.ui.presenters.contacts.contacts_list.IContactItemView
import ru.myproevent.ui.presenters.events.event.participant_pickers.participant_from_contacts_picker.adapters.IContactPickerPresenter
import ru.myproevent.ui.presenters.events.event.participant_pickers.participant_from_contacts_picker.adapters.IPickedContactsListPresenter
import javax.inject.Inject


// TODO: отрефаторить: этот презентер практически копирует ContactsPresenter. Как делегировать ContactsPresenter, то есть как сделать композицию?
//             Проблема в том что яне разобрался как передать viewState
//             Сделать общий презентер для ContactsFragment и ParticipantFromContactsPickerFragment?
class ParticipantFromContactsPickerPresenter(
    localRouter: Router,
    private val eventParticipantsIds: List<Long>
) :
    BaseMvpPresenter<ParticipantFromContactsPickerView>(localRouter) {

    private val pickedParticipants = arrayListOf<Contact>()

    private var currOptionsCount = 0

    @Inject
    lateinit var contactsRepository: IProEventContactsRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    inner class ContactListPresenter(
        var itemClickListener: ((IContactItemView, Contact) -> Unit)? = null,
        var statusClickListener: ((Contact) -> Unit)? = null
    ) : IContactPickerPresenter {

        private val contactDTOs = mutableListOf<ContactDto>()

        private var size = 0

        private var contacts = mutableListOf<Contact?>()

        override fun getCount() = size

        override fun bindView(view: IContactPickerItemView) {
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
                            status = Contact.Status.fromString(contactDto.status)
                        )
                        fillItemView(view, contacts[pos]!!)
                    }).disposeOnDestroy()
            }
        }

        private fun fillItemView(view: IContactPickerItemView, contact: Contact) {
            contact.apply {
                if (!fullName.isNullOrEmpty()) {
                    view.setName(fullName!!)
                } else if (!nickName.isNullOrEmpty()) {
                    view.setName(nickName!!)
                } else {
                    view.setName(id.toString())
                }
                if (eventParticipantsIds.contains(contact.id)) {
                    view.setDescription(getString(R.string.already_participant_01))
                } else if (!description.isNullOrEmpty()) {
                    view.setDescription(description!!)
                } else if (!nickName.isNullOrEmpty()) {
                    view.setDescription(nickName!!)
                } else {
                    view.setDescription(getString(R.string.user_id, id))
                }
                status?.let { view.setStatus(it) }

                view.setSelection(pickedParticipants.contains(contact))
            }
        }

        override fun onItemClick(view: IContactPickerItemView) {
            contacts[view.pos]?.let {
                if (eventParticipantsIds.contains(it.id)) {
                    viewState.showMessage(getString(R.string.already_participant_02))
                    return
                }
                itemClickListener?.invoke(view, it)
            }
        }

        override fun onStatusClick(view: IContactPickerItemView) {
            contacts[view.pos]?.let { statusClickListener?.invoke(it) }
        }

        fun setData(data: List<ContactDto>, size: Int) {
            this.size = 0
            contactDTOs.clear()
            contactDTOs.addAll(data)
            contacts = MutableList(size) { null }
            this.size = size
            currOptionsCount = this.size
            viewState.setPickedParticipantsCount(
                pickedParticipants.size,
                currOptionsCount
            )
            viewState.updateContactsList()
        }
    }

    inner class PickedContactsListPresenter(
        private var itemClickListener: ((IPickedContactItemView, Contact) -> Unit)? = null,
    ) : IPickedContactsListPresenter {
        override fun getCount() = pickedParticipants.size

        override fun bindView(view: IPickedContactItemView) = with(view) {
            pickedParticipants[pos].fullName?.let {
                setName(it)
            } ?: pickedParticipants[pos].nickName?.let {
                setName(it)
            } ?: setName("#${pickedParticipants[pos].id}")
        }

        override fun onItemClick(view: IPickedContactItemView) {
            pickedParticipants[view.pos].let { itemClickListener?.invoke(view, it) }
        }
    }

    val contactsPickerListPresenter: ParticipantFromContactsPickerPresenter.ContactListPresenter =
        ContactListPresenter({ _, contact ->
            if (pickedParticipants.contains(contact)) {
                pickedParticipants.remove(contact)
                if (pickedParticipants.isEmpty()) {
                    viewState.hidePickedParticipants()
                }
            } else {
                pickedParticipants.add(contact)
                viewState.showPickedParticipants()
            }
            viewState.setPickedParticipantsCount(
                pickedParticipants.size,
                currOptionsCount
            )
            viewState.updatePickedContactsList()
            viewState.updateContactsList()
        }, { contact ->
            val action = when (contact.status) {
                Contact.Status.DECLINED -> Contact.Action.DELETE
                Contact.Status.PENDING -> Contact.Action.CANCEL
                Contact.Status.REQUESTED -> Contact.Action.ACCEPT
                else -> return@ContactListPresenter
            }

            val message = getString(
                when (action) {
                    Contact.Action.ACCEPT -> R.string.accept_contact_request_question
                    Contact.Action.CANCEL -> R.string.cancel_request_question
                    Contact.Action.DECLINE -> R.string.decline_contact_request_question
                    Contact.Action.DELETE -> R.string.delete_contact_question
                    else -> R.string.empty_string
                }
            )

            viewState.showConfirmationScreen(message) { confirmed ->
                viewState.hideConfirmationScreen()
                if (!confirmed) return@showConfirmationScreen
                performActionOnContact(contact, action)
            }
        })

    val pickedContactsListPresenter = PickedContactsListPresenter { _, contact ->
        pickedParticipants.remove(contact)
        viewState.setPickedParticipantsCount(
            pickedParticipants.size,
            currOptionsCount
        )
        viewState.updatePickedContactsList()
        viewState.updateContactsList()
        if (pickedParticipants.isEmpty()) {
            viewState.hidePickedParticipants()
        }
    }

    private fun performActionOnContact(contact: Contact, action: Contact.Action) {
        when (action) {
            Contact.Action.ACCEPT -> contactsRepository.acceptContact(contact.id)
            Contact.Action.CANCEL, Contact.Action.DELETE -> contactsRepository.deleteContact(contact.id)
            Contact.Action.DECLINE -> contactsRepository.declineContact(contact.id)
            else -> return
        }.observeOn(uiScheduler)
            .subscribe({ loadData() }, { viewState.showToast(getString(R.string.action_failed)) })
            .disposeOnDestroy()
    }

    fun init() {
        viewState.init()
        loadData()
    }

    fun loadData(status: Contact.Status = Contact.Status.ALL) {
        contactsRepository.getContacts(status)
            .observeOn(uiScheduler)
            .subscribe({ data ->
                contactsPickerListPresenter.setData(data.map { it.toContactDto() }, data.size)
            }, {
                viewState.showToast(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()
    }

    fun confirmPick() {
        Log.d("[MYLOG]", "presenter confirmPick")
        if (pickedParticipants.isEmpty()) {
            viewState.showMessage(getString(R.string.at_least_1_contact_must_be_selected))
            return
        }
        viewState.setResult(
            PARTICIPANTS_PICKER_RESULT_KEY,
            Bundle().apply {
                putParcelableArray(CONTACTS_KEY, pickedParticipants.toTypedArray())
            })
        localRouter.backTo(screens.currentlyOpenEventScreen())
    }
}
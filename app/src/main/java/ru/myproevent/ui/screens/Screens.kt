package ru.myproevent.ui.screens

import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.entities.Address
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.ui.fragments.*
import ru.myproevent.ui.fragments.authorization.*
import ru.myproevent.ui.fragments.chat.Chat1Fragment
import ru.myproevent.ui.fragments.chat.ChatFragment
import ru.myproevent.ui.fragments.chat.ChatsFragment
import ru.myproevent.ui.fragments.contacts.ContactAddFragment
import ru.myproevent.ui.fragments.contacts.ContactFragment
import ru.myproevent.ui.fragments.contacts.ContactsFragment
import ru.myproevent.ui.fragments.events.event.AddEventPlaceFragment
import ru.myproevent.ui.fragments.events.event.EventActionConfirmationFragment
import ru.myproevent.ui.fragments.events.EventsFragment
import ru.myproevent.ui.fragments.events.event.EventFragment
import ru.myproevent.ui.fragments.events.event.EventParticipantFragment
import ru.myproevent.ui.fragments.events.event.participant_pickers.ParticipantByEmailPickerFragment
import ru.myproevent.ui.fragments.events.event.participant_pickers.ParticipantFromContactsPickerFragment
import ru.myproevent.ui.fragments.events.event.participant_pickers.ParticipantPickerTypeSelectionFragment
import ru.myproevent.ui.fragments.settings.AccountFragment
import ru.myproevent.ui.fragments.settings.SecurityFragment
import ru.myproevent.ui.fragments.settings.SettingsFragment

class Screens : IScreens {
    override fun authorization() = FragmentScreen { AuthorizationFragment.newInstance() }
    override fun home() = FragmentScreen { HomeFragment.newInstance() }
    override fun settings() = FragmentScreen { SettingsFragment.newInstance() }
    override fun registration() = FragmentScreen { RegistrationFragment.newInstance() }
    override fun code() = FragmentScreen { CodeFragment.newInstance() }
    override fun login() = FragmentScreen { LoginFragment.newInstance() }
    override fun recovery() = FragmentScreen { RecoveryFragment.newInstance() }
    override fun account() = FragmentScreen { AccountFragment.newInstance() }
    override fun security() = FragmentScreen { SecurityFragment.newInstance() }
    override fun contacts() = FragmentScreen { ContactsFragment.newInstance() }
    override fun contact(contact: Contact): Screen = FragmentScreen { ContactFragment.newInstance(contact) }
    override fun contactAdd(): Screen = FragmentScreen { ContactAddFragment.newInstance() }
    override fun chat() = FragmentScreen { ChatFragment.newInstance() }
    override fun chat1() = FragmentScreen { Chat1Fragment.newInstance() }
    override fun chats() = FragmentScreen { ChatsFragment.newInstance() }
    override fun events() = FragmentScreen { EventsFragment.newInstance() }
    override fun event() = FragmentScreen("EVENT") { EventFragment.newInstance() }
    override fun event(event: Event) = FragmentScreen("EVENT") { EventFragment.newInstance(event) }
    override fun currentlyOpenEventScreen() = FragmentScreen("EVENT") { throw RuntimeException("В текущем стеке нет экрана Screens.event") }
    override fun eventActionConfirmation(event: Event, status: Event.Status?) = FragmentScreen { EventActionConfirmationFragment.newInstance(event, status) }
    override fun participantPickerTypeSelection(participantsIds: List<Long>) = FragmentScreen { ParticipantPickerTypeSelectionFragment.newInstance(participantsIds) }
    override fun participantFromContactsPicker(participantsIds: List<Long>) = FragmentScreen { ParticipantFromContactsPickerFragment.newInstance(participantsIds) }
    override fun participantByEmailPicker() = FragmentScreen { ParticipantByEmailPickerFragment.newInstance() }
    override fun addEventPlace(address: Address?): Screen = FragmentScreen { AddEventPlaceFragment.newInstance(address)}
    override fun eventParticipant(profileDto: ProfileDto) = FragmentScreen { EventParticipantFragment.newInstance(profileDto) }
    override fun newPassword(email: String) = FragmentScreen { NewPasswordFragment.newInstance(email) }
}
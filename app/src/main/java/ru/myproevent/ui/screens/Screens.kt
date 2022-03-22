package ru.myproevent.ui.screens

import android.net.Uri
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.myproevent.domain.models.entities.*
import ru.myproevent.ui.fragments.*
import ru.myproevent.ui.fragments.authorization.*
import ru.myproevent.ui.fragments.chat.Chat1Fragment
import ru.myproevent.ui.fragments.chat.ChatFragment
import ru.myproevent.ui.fragments.chat.ChatsFragment
import ru.myproevent.ui.fragments.contacts.ContactAddFragment
import ru.myproevent.ui.fragments.contacts.ContactFragment
import ru.myproevent.ui.fragments.contacts.ContactsFragment
import ru.myproevent.ui.fragments.events.EventsFragment
import ru.myproevent.ui.fragments.events.event.*
import ru.myproevent.ui.fragments.events.event.event_map.EventMapFragment
import ru.myproevent.ui.fragments.events.event.map_image_picker.MapCreatorFragment
import ru.myproevent.ui.fragments.events.event.map_image_picker.MapImagePickerFragment
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
    override fun contact(profileId: Long): Screen = FragmentScreen { ContactFragment.newInstance(profileId) }
    override fun contactAdd(): Screen = FragmentScreen { ContactAddFragment.newInstance() }
    override fun chat() = FragmentScreen { ChatFragment.newInstance() }
    override fun chat1() = FragmentScreen { Chat1Fragment.newInstance() }
    override fun chats() = FragmentScreen { ChatsFragment.newInstance() }
    override fun currentlyOpenEventScreen() = FragmentScreen("EVENT") { throw RuntimeException("В текущем стеке нет экрана Screens.event") }
    override fun participantPickerTypeSelection(participantsIds: List<Long>) = FragmentScreen { ParticipantPickerTypeSelectionFragment.newInstance(participantsIds) }
    override fun participantFromContactsPicker(participantsIds: List<Long>) = FragmentScreen { ParticipantFromContactsPickerFragment.newInstance(participantsIds) }
    override fun participantByEmailPicker() = FragmentScreen { ParticipantByEmailPickerFragment.newInstance() }
    override fun addEventPlace(address: Address?): Screen = FragmentScreen { AddEventPlaceFragment.newInstance(address) }
    override fun newPassword(email: String) = FragmentScreen { NewPasswordFragment.newInstance(email) }

    // Events screens
    override fun eventDatesPicker(dates: TimeInterval?) = FragmentScreen { EventDatesPickerFragment.newInstance(dates) }
    override fun eventParticipant(profile: Profile) = FragmentScreen { EventParticipantFragment.newInstance(profile) }
    override fun eventActionConfirmation(event: Event, status: Event.Status?) = FragmentScreen { EventActionConfirmationFragment.newInstance(event, status) }
    override fun events() = FragmentScreen { EventsFragment.newInstance() }
    override fun event() = FragmentScreen("EVENT") { EventFragment.newInstance() }
    override fun event(event: Event) = FragmentScreen("EVENT") { EventFragment.newInstance(event) }
    override fun eventMap() = FragmentScreen { EventMapFragment.newInstance() }
    override fun mapImagePicker(uri: Uri) = FragmentScreen { MapImagePickerFragment.newInstance(uri) }
    override fun mapCreator(mapImageUUID: String) = FragmentScreen { MapCreatorFragment.newInstance(mapImageUUID) }
}

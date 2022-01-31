package ru.myproevent.ui.presenters.events.event

import android.util.Log
import com.github.terrakok.cicerone.Router
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.models.UUIDBody
import ru.myproevent.domain.models.entities.Address
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.repositories.events.IProEventEventsRepository
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.io.File
import java.util.*
import javax.inject.Inject

class EventPresenter(localRouter: Router) : BaseMvpPresenter<EventView>(localRouter) {
    private val pickedParticipantsIds = mutableListOf<Long>()
    private var isParticipantsProfilesInitialized = false

    @Inject
    lateinit var eventsRepository: IProEventEventsRepository

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    @Inject
    lateinit var imagesRepository: IImagesRepository

    fun addEvent(
        name: String,
        startDate: Date,
        endDate: Date,
        address: Address?,
        description: String,
        callback: ((Event?) -> Unit)? = null
    ) {
        eventsRepository
            .saveEvent(
                Event(
                    id = null,
                    name = name,
                    ownerUserId = loginRepository.getLocalId()!!,
                    eventStatus = Event.Status.ACTUAL,
                    startDate = startDate,
                    endDate = endDate,
                    description = description,
                    participantsUserIds = pickedParticipantsIds.toLongArray(),
                    city = null,
                    address = address,
                    mapsFileIds = null,
                    pointsPointIds = null,
                    imageFile = null
                )
            )
            .observeOn(uiScheduler)
            .subscribe({
                callback?.invoke(it)
                viewState.showMessage("Мероприятие создано")
                viewState.hideEditOptions()
                viewState.showActionOptions()
            }, {
                callback?.invoke(null)
                viewState.showMessage("ПРОИЗОШЛА ОШИБКА: ${it.message}")
            }).disposeOnDestroy()
    }

    fun editEvent(event: Event, callback: ((Event?) -> Unit)? = null) {
        event.participantsUserIds = pickedParticipantsIds.toLongArray()
        Log.d("[REMOVE]", "presenter.editEvent(): $event")
        eventsRepository
            .editEvent(event)
            .observeOn(uiScheduler)
            .subscribe({
                callback?.invoke(event)
                viewState.showMessage("Изменения сохранены")
            }, {
                callback?.invoke(null)
                viewState.showMessage("ПРОИЗОШЛА ОШИБКА: ${it.message}")
            }).disposeOnDestroy()
    }

    fun finishEvent(event: Event) =
        localRouter.navigateTo(screens.eventActionConfirmation(event, Event.Status.COMPLETED))


    fun cancelEvent(event: Event) =
        localRouter.navigateTo(screens.eventActionConfirmation(event, Event.Status.CANCELLED))

    fun deleteEvent(event: Event) =
        localRouter.navigateTo(screens.eventActionConfirmation(event, null))

    fun copyEvent(event: Event) {
        event.ownerUserId = loginRepository.getLocalId()!!
        event.eventStatus = Event.Status.ACTUAL
        eventsRepository
            .saveEvent(event)
            .observeOn(uiScheduler)
            .subscribe({
                localRouter.navigateTo(screens.event(it))
            }, {
                viewState.showMessage("ПРОИЗОШЛА ОШИБКА: ${it.message}")
            }).disposeOnDestroy()
    }

    fun pickParticipants() {
        localRouter.navigateTo(screens.participantPickerTypeSelection(pickedParticipantsIds))
    }

    fun openParticipant(profileDto: ProfileDto) {
        localRouter.navigateTo(screens.eventParticipant(profileDto))
    }

    private fun addParticipantItemView(profileDto: ProfileDto) {
        viewState.addParticipantItemView(profileDto)
        pickedParticipantsIds.add(profileDto.userId)
    }

    fun initParticipantsProfiles(participantsIds: LongArray) {
        if (isParticipantsProfilesInitialized) {
            return
        }
        isParticipantsProfilesInitialized = true
        for (id in participantsIds) {
            profilesRepository.getProfile(id)
                .observeOn(uiScheduler)
                .subscribe({ profileDto ->
                    addParticipantItemView(profileDto!!)
                }, {
                    Log.d("[FUCK]", "error: $it")
                    val profileDto = ProfileDto(
                        userId = id,
                        fullName = "Заглушка",
                        description = "Профиля нет, или не загрузился",
                    )
                    addParticipantItemView(profileDto)
                }).disposeOnDestroy()
        }
    }

    fun addParticipantsProfiles(participants: Array<ProfileDto>) {
        for (participant in participants) {
            addParticipantItemView(participant)
        }
    }

    fun clearParticipants() {
        viewState.clearParticipants()
        pickedParticipantsIds.clear()
        isParticipantsProfilesInitialized = false
    }

    fun addEventPlace(address: Address?) {
        localRouter.navigateTo(screens.addEventPlace(address))
    }

    fun enableDescriptionEdit() {
        viewState.enableDescriptionEdit()
    }

    fun expandDescription() {
        viewState.expandDescription()
    }

    fun expandMaps() {
        viewState.expandMaps()
    }

    fun expandPoints() {
        viewState.expandPoints()
    }

    fun expandParticipants() {
        viewState.expandParticipants()
    }

    fun cancelEdit() {
        viewState.cancelEdit()
    }

    fun hideEditOptions() {
        viewState.hideEditOptions()
    }

    fun lockEdit() {
        viewState.lockEdit()
    }

    fun showMessage(message: String) {
        viewState.showMessage(message)
    }

    fun showAbsoluteBar(
        title: String,
        iconResource: Int?,
        iconTintResource: Int?,
        onCollapseScroll: Int,
        onCollapse: () -> Unit,
        onEdit: () -> Unit
    ) {
        viewState.showAbsoluteBar(
            title,
            iconResource,
            iconTintResource,
            onCollapseScroll,
            onCollapse,
            onEdit
        )
    }

    fun saveImage(file: File): String {
        return imagesRepository.saveImage(file)
    }

    fun hideAbsoluteBar() {
        viewState.hideAbsoluteBar()
    }

    fun unlockNameEdit() {
        viewState.unlockNameEdit()
    }

    fun unlockDateEdit() {
        viewState.unlockDateEdit()
    }

    fun unlockLocationEdit() {
        viewState.unlockLocationEdit()
    }

    fun removeParticipant(id: Long) {
        // .toList() используется чтобы передать именно копию pickedParticipantsIds, а не ссылку
        viewState.removeParticipant(id, pickedParticipantsIds.toList())
        pickedParticipantsIds.remove(id)
    }

    fun showEditOptions() {
        viewState.showEditOptions()
    }
}
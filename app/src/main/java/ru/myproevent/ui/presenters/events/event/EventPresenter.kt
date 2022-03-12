package ru.myproevent.ui.presenters.events.event

import android.widget.Toast
import com.github.terrakok.cicerone.Router
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.domain.models.entities.Address
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.entities.TimeInterval
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

    val pickedDates = mutableListOf<TimeInterval>()
    private var isDatesInitialized = false

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
        dates: TreeSet<TimeInterval?>,
        address: Address?,
        description: String,
        uuid: String?,
        callback: ((Event?) -> Unit)? = null
    ) {
        eventsRepository
            .saveEvent(
                Event(
                    id = null,
                    name = name,
                    ownerUserId = loginRepository.getLocalId()!!,
                    status = Event.Status.ACTUAL,
                    dates = dates,
                    description = description,
                    participantsUserIds = pickedParticipantsIds.toLongArray(),
                    city = null,
                    address = address,
                    mapsFileIds = null,
                    pointsPointIds = null,
                    imageFile = uuid
                )
            )
            .observeOn(uiScheduler)
            .subscribe({
                callback?.invoke(it)
                viewState.showMessage(getString(R.string.event_created))
                viewState.hideEditOptions()
                viewState.showActionOptions()
            }, {
                callback?.invoke(null)
                viewState.showMessage(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()
    }

    fun editEvent(event: Event, callback: ((Event?) -> Unit)? = null) {
        event.participantsUserIds = pickedParticipantsIds.toLongArray()
        eventsRepository
            .editEvent(event)
            .observeOn(uiScheduler)
            .subscribe({
                callback?.invoke(event)
                viewState.showMessage(getString(R.string.changes_saved))
            }, {
                callback?.invoke(null)
                viewState.showMessage(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()
    }

    fun saveImage(file: File, callback: ((String?) -> Unit)? = null) {
        imagesRepository
            .saveImage(file)
            .observeOn(uiScheduler)
            .subscribe({
                callback?.invoke(it.uuid)
            }, {
                callback?.invoke(null)
            }).disposeOnDestroy()
    }

    fun deleteImage(uuid: String) {
        imagesRepository.deleteImage(uuid).subscribe().disposeOnDestroy()
    }

    fun finishEvent(event: Event) =
        localRouter.navigateTo(screens.eventActionConfirmation(event, Event.Status.COMPLETED))

    fun cancelEvent(event: Event) =
        localRouter.navigateTo(screens.eventActionConfirmation(event, Event.Status.CANCELLED))

    fun deleteEvent(event: Event) =
        localRouter.navigateTo(screens.eventActionConfirmation(event, null))

    fun copyEvent(event: Event) {
        event.ownerUserId = loginRepository.getLocalId()!!
        event.status = Event.Status.ACTUAL
        eventsRepository
            .saveEvent(event)
            .observeOn(uiScheduler)
            .subscribe({
                localRouter.navigateTo(screens.event(it))
            }, {
                viewState.showMessage(getString(R.string.error_occurred, it.message))
            }).disposeOnDestroy()
    }

    fun pickParticipants() {
        localRouter.navigateTo(screens.participantPickerTypeSelection(pickedParticipantsIds))
    }

    fun pickDates() {
        Toast.makeText(ProEventApp.instance, "EventPresenter::pickDates call", Toast.LENGTH_LONG)
            .show()
    }

    fun openParticipant(profile: Profile) {
        localRouter.navigateTo(screens.eventParticipant(profile))
    }

    fun openDateEditOptions(timeInterval: TimeInterval) {
        viewState.showDateEditOptions(pickedDates.indexOf(timeInterval))
    }

    private fun addParticipantItemView(profile: Profile) {
        viewState.addParticipantItemView(profile)
        pickedParticipantsIds.add(profile.id)
    }

    private fun addDateItemView(timeInterval: TimeInterval) {
        val datePosition =
            pickedDates.indexOfLast { currTimeInterval -> return@indexOfLast currTimeInterval.start <= timeInterval.start } + 1
        viewState.addDateItemView(
            timeInterval,
            datePosition
        )
        pickedDates.add(datePosition, timeInterval)
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
                    val profile = Profile(
                        id = id,
                        fullName = "Заглушка",
                        description = "Профиля нет, или не загрузился",
                    )
                    addParticipantItemView(profile)
                }).disposeOnDestroy()
        }
    }

    fun initDates(dates: TreeSet<TimeInterval?>) {
        if (isDatesInitialized) {
            return
        }
        isDatesInitialized = true
        for (date in dates) {
            date?.let { addDateItemView(it) }
        }
    }

    fun addParticipantsProfiles(participants: Array<Profile>) {
        for (participant in participants) {
            addParticipantItemView(participant)
        }
    }

    fun datePickerFragment(timeInterval: TimeInterval?) {
        localRouter.navigateTo(screens.eventDatesPicker(timeInterval))
    }

    fun addEventDate(timeInterval: TimeInterval) {
        addDateItemView(timeInterval)
    }

    fun clearDates() {
        viewState.clearDates()
        pickedDates.clear()
        isDatesInitialized = false
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

    fun expandDates() {
        viewState.expandDates()
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

    fun hideAbsoluteBar() {
        viewState.hideAbsoluteBar()
    }

    fun unlockNameEdit() {
        viewState.unlockNameEdit()
    }

    fun unlockLocationEdit() {
        viewState.unlockLocationEdit()
    }

    fun removeParticipant(id: Long) {
        // .toList() используется чтобы передать именно копию pickedParticipantsIds, а не ссылку
        viewState.removeParticipant(id, pickedParticipantsIds.toList())
        pickedParticipantsIds.remove(id)
    }

    fun removeDate(timeInterval: TimeInterval) {
        viewState.removeDate(timeInterval, pickedDates.toList())
        pickedDates.remove(timeInterval)
    }

    fun removeDate(position: Int) {
        viewState.removeDate(pickedDates[position], pickedDates.toList())
        pickedDates.removeAt(position)
    }

    fun editDate(position: Int) {
        Toast.makeText(
            ProEventApp.instance,
            "EventPresenter::editDate call;\npickedDates[position]: ${pickedDates[position]};",
            Toast.LENGTH_LONG
        )
            .show()
    }

    fun showEditOptions() {
        viewState.showEditOptions()
    }

    fun hideDateEditOptions() {
        viewState.hideDateEditOptions()
    }
}
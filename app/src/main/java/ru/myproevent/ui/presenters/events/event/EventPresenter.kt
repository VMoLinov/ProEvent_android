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
import ru.myproevent.ui.adapters.event_items.*
import ru.myproevent.ui.data_items.EventScreenItemsFactory
import ru.myproevent.ui.data_items.no_items_placeholders.NoDatesPlaceholderFactory
import ru.myproevent.ui.data_items.no_items_placeholders.NoItemsPlaceholderFactory
import ru.myproevent.ui.presenters.BaseMvpPresenter
import java.io.File
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject

class EventPresenter(localRouter: Router, var eventBeforeEdit: Event?) :
    BaseMvpPresenter<EventView>(localRouter) {

    @Inject
    lateinit var eventsRepository: IProEventEventsRepository

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    @Inject
    lateinit var profilesRepository: IProEventProfilesRepository

    @Inject
    lateinit var imagesRepository: IImagesRepository

    val isCurrentUserOwnsEvent by lazy {
        eventBeforeEdit?.let { loginRepository.getLocalId()!! == it.ownerUserId } ?: true
    }

    fun getEventScreenItemsBeforeEdit() = EventScreenItemsFactory.create(eventPresenter = this)

    val eventScreenListPresenter = EventScreenListPresenter(eventPresenter = this)

    val eventEditOptionsPresenter = EventEditOptionsPresenter(eventPresenter = this)

    val pickedParticipantsIds: List<Long>
        get() {
            val participantsListItems =
                (eventScreenListPresenter.eventScreenItems.find { item -> item.itemId == EVENT_SCREEN_ITEM_ID.PARTICIPANTS_HEADER } as EventScreenItem.FormsHeader<EventScreenItem.ListItem>).items
            return if (participantsListItems.first() is EventScreenItem.NoItemsPlaceholder) {
                listOf()
            } else {
                with(participantsListItems.iterator()) {
                    List(participantsListItems.size) {
                        (next() as EventScreenItem.ParticipantItem).participantId
                    }
                }
            }
        }
    private var isParticipantsProfilesInitialized = false

    val pickedDates = mutableListOf<TimeInterval>()
    private var isDatesInitialized = false

    fun addEvent(
        name: String,
        dates: TreeSet<TimeInterval>,
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
                viewState.enableActionOptions()
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

    private fun finishEvent() =
        localRouter.navigateTo(
            screens.eventActionConfirmation(
                eventBeforeEdit!!,
                Event.Status.COMPLETED
            )
        )

    fun cancelEvent(event: Event) =
        localRouter.navigateTo(screens.eventActionConfirmation(event, Event.Status.CANCELLED))

    private fun deleteEvent() =
        localRouter.navigateTo(screens.eventActionConfirmation(eventBeforeEdit!!, null))

    private fun copyEvent() {
        // TODO: вывести предпреждения что не сохранённые данные не будут перенесены в копию? Уточнить у дизайнера
        val event = eventBeforeEdit!!
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

    // TODO: отрефакторить: копирует addParticipantsProfiles
    private fun addDateItemView(timeInterval: TimeInterval) {
        // TODO: отрефакторить: перенести это в eventScreenListPresenter
        val headerPosition =
            eventScreenListPresenter.eventScreenItems.indexOfFirst { item -> item.itemId == EVENT_SCREEN_ITEM_ID.DATES_HEADER }
        with(eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
            if (items.first() is EventScreenItem.NoItemsPlaceholder) {
                items.clear()
                if (isExpanded) {
                    eventScreenListPresenter.eventScreenItems.subList(
                        headerPosition + 1,
                        headerPosition + 2
                    ).clear()
                }
            }
            val listItemPosition =
                with(items.indexOfFirst { item -> (item as EventScreenItem.EventDateItem).timeInterval.start > timeInterval.start }) {
                    if (this == -1) {
                        items.size
                    } else {
                        this
                    }
                }
            // TODO: ВОЗМОЖНЫЙ БАГ: НУЖНО ЛИ ДОБАЛЯТЬ 1?
            val screenItemPosition = headerPosition + 1 + listItemPosition
            items.add(
                EventScreenItem.EventDateItem(
                    timeInterval = timeInterval,
                    header = this
                )
            )
            if (isExpanded) {
                eventScreenListPresenter.eventScreenItems.add(
                    screenItemPosition,
                    EventScreenItem.EventDateItem(
                        timeInterval = timeInterval,
                        header = this
                    )
                )
            }
            if (!isExpanded) {
                isExpanded = true
                eventScreenListPresenter.formsHeaderItemPresenter.setOfExpandedItems.add(EVENT_SCREEN_ITEM_ID.DATES_HEADER)
                eventScreenListPresenter.eventScreenItems.addAll(headerPosition + 1, items)
            }
        }
        viewState.updateEventScreenList()
        viewState.showEditOptions()
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

    // TODO: отрефакторить: передавать только id
    fun addParticipantsProfiles(participants: Array<Profile>) {
        // TODO: отрефакторить: перенести это в eventScreenListPresenter
        val headerPosition =
            eventScreenListPresenter.eventScreenItems.indexOfFirst { item -> item.itemId == EVENT_SCREEN_ITEM_ID.PARTICIPANTS_HEADER }
        with(eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
            if (items.first() is EventScreenItem.NoItemsPlaceholder) {
                items.clear()
                if (isExpanded) {
                    eventScreenListPresenter.eventScreenItems.subList(
                        headerPosition + 1,
                        headerPosition + 2
                    ).clear()
                }
            }
            for (participantProfile in participants) {
                eventScreenListPresenter.participantItemPresenter.participantProfiles[participantProfile.id] = participantProfile
                items.add(
                    EventScreenItem.ParticipantItem(
                        participantId = participantProfile.id,
                        header = this
                    )
                )
                if (isExpanded) {
                    // TODO: я не понял почему не нужно добавлять + 1 к индексу
                    val prevLastItemPosition = headerPosition + items.size
                    eventScreenListPresenter.eventScreenItems.add(
                        prevLastItemPosition,
                        EventScreenItem.ParticipantItem(
                            participantId = participantProfile.id,
                            header = this
                        )
                    )
                }
            }
            if (!isExpanded) {
                isExpanded = true
                eventScreenListPresenter.formsHeaderItemPresenter.setOfExpandedItems.add(EVENT_SCREEN_ITEM_ID.PARTICIPANTS_HEADER)
                eventScreenListPresenter.eventScreenItems.addAll(headerPosition + 1, items)
            }
        }

        viewState.updateEventScreenList()
        viewState.showEditOptions()
    }

    fun openDatePicker(timeInterval: TimeInterval?) {
        localRouter.navigateTo(screens.eventDatesPicker(timeInterval))
    }

    fun addEventDate(timeInterval: TimeInterval) {
        addDateItemView(timeInterval)
    }

    // TODO: отрефакторить: копирует removeDateItem()
    private fun removeParticipantItem(id: Long) {
        val headerPosition =
            eventScreenListPresenter.eventScreenItems.indexOfFirst { item -> item.itemId == EVENT_SCREEN_ITEM_ID.PARTICIPANTS_HEADER }
        with(eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
            val indexOfItemToRemove =
                items.indexOfFirst { item -> (item as EventScreenItem.ParticipantItem).participantId == id }
            if (indexOfItemToRemove == -1) {
                throw RuntimeException("Попытка удалить участника, который не явялется участником редактируемого мероприятия.")
            }
            items.removeIf { item -> (item as EventScreenItem.ParticipantItem).participantId == id }
            if (!isExpanded) {
                isExpanded = true
                eventScreenListPresenter.formsHeaderItemPresenter.setOfExpandedItems.add(EVENT_SCREEN_ITEM_ID.DATES_HEADER)
            } else {
                eventScreenListPresenter.eventScreenItems.removeAt(headerPosition + 1 + indexOfItemToRemove)
            }
            if (items.isEmpty()) {
                items.add(NoItemsPlaceholderFactory.create(header = this))
                eventScreenListPresenter.eventScreenItems.add(
                    headerPosition + 1,
                    items.first()
                )
            }
        }
        viewState.updateEventScreenList()
        viewState.showEditOptions()
    }

    fun removeParticipant(id: Long) {
        removeParticipantItem(id)
    }

    private fun removeDateItem(timeInterval: TimeInterval) {
        val headerPosition =
            eventScreenListPresenter.eventScreenItems.indexOfFirst { item -> item.itemId == EVENT_SCREEN_ITEM_ID.DATES_HEADER }
        with(eventScreenListPresenter.eventScreenItems[headerPosition] as EventScreenItem.FormsHeader<EventScreenItem.ListItem>) {
            val indexOfItemToRemove =
                items.indexOfFirst { item -> 
                    (item as EventScreenItem.EventDateItem).timeInterval == timeInterval }
            if (indexOfItemToRemove == -1) {
                throw RuntimeException("Попытка удалить дату(временной интервал), которая отсутствует в датах редактируемого мероприятия.")
            }
            items.removeIf { item -> (item as EventScreenItem.EventDateItem).timeInterval == timeInterval }
            if (!isExpanded) {
                isExpanded = true
                eventScreenListPresenter.formsHeaderItemPresenter.setOfExpandedItems.add(EVENT_SCREEN_ITEM_ID.DATES_HEADER)
            } else {
                eventScreenListPresenter.eventScreenItems.removeAt(headerPosition + 1 + indexOfItemToRemove)
            }
            if (items.isEmpty()) {
                items.add(NoDatesPlaceholderFactory.create(header = this))
                eventScreenListPresenter.eventScreenItems.add(
                    headerPosition + 1,
                    items.first()
                )
            }
        }
        viewState.updateEventScreenList()
        viewState.showEditOptions()
    }

    fun removeDate(timeInterval: TimeInterval) {
        removeDateItem(timeInterval)
    }

    private fun loadProfiles() {
        for (participantItem in (eventScreenListPresenter.eventScreenItems[7] as EventScreenItem.FormsHeader<*>).items) {
            if (participantItem is EventScreenItem.NoItemsPlaceholder) {
                return
            }
            profilesRepository.getProfile((participantItem as EventScreenItem.ParticipantItem).participantId)
                .observeOn(uiScheduler)
                .subscribe({ profileDto ->
                    eventScreenListPresenter.participantItemPresenter.participantProfiles[participantItem.participantId] = profileDto!!
                    viewState.updateEventScreenList()
                }, {
                    val profile = Profile(
                        id = participantItem.participantId,
                        fullName = "[ОШИБКА]",
                        description = "Профиля нет, или не загрузился",
                    )
                    eventScreenListPresenter.participantItemPresenter.participantProfiles[participantItem.participantId] = profile
                    viewState.updateEventScreenList()
                }).disposeOnDestroy()
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        eventScreenListPresenter.eventScreenItems.addAll(getEventScreenItemsBeforeEdit())
        viewState.init()
        if (eventBeforeEdit != null) {
            viewState.enableActionOptions()
        } else {
            viewState.showEditOptions()
        }
        loadProfiles()
    }

    fun getEventActionOptions(): List<Pair<String, () -> Unit>> {
        return if (eventBeforeEdit!!.ownerUserId == loginRepository.getLocalId()!!) {
            if (eventBeforeEdit!!.status == Event.Status.ACTUAL) {
                listOf(
                    Pair("Заваершить мероприятие") { finishEvent() },
                    Pair("Скопировать мероприятие") { copyEvent() },
                    Pair("Удалить мероприятие") { deleteEvent() }
                )
            } else {
                listOf(
                    Pair("Скопировать мероприятие") { copyEvent() },
                    Pair("Удалить мероприятие") { deleteEvent() }
                )
            }
        } else {
            listOf(
                Pair("Скопировать мероприятие") { copyEvent() },
            )
        }
    }
}
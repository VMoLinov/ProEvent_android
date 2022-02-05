package ru.myproevent.ui.presenters.events.event.confirmation

import com.github.terrakok.cicerone.Router
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.repositories.events.IProEventEventsRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class EventActionConfirmPresenter(localRouter: Router) :
    BaseMvpPresenter<EventActionConfirmView>(localRouter) {
    @Inject
    lateinit var eventsRepository: IProEventEventsRepository

    fun editStatus(event: Event, status: Event.Status) {
        val originalStatus = event.status
        event.status = status
        eventsRepository
            .editEvent(event)
            .observeOn(uiScheduler)
            .subscribe({
                when (status) {
                    Event.Status.COMPLETED -> viewState.showMessage("Мероприятие завершено")
                    Event.Status.CANCELLED -> viewState.showMessage("Мероприятие отменено")
                }
                event.status = status
                onBackPressed()
            }, {
                viewState.showMessage("ПРОИЗОШЛА ОШИБКА: ${it.message}")
            }).disposeOnDestroy()
        event.status = originalStatus
    }

    fun deleteEvent(event: Event) {
        eventsRepository
            .deleteEvent(event)
            .observeOn(uiScheduler)
            .subscribe({
                viewState.showMessage("Мероприятие удалено")
                localRouter.newRootScreen(screens.events())
            }, {
                viewState.showMessage("ПРОИЗОШЛА ОШИБКА: ${it.message}")
            }).disposeOnDestroy()
    }
}
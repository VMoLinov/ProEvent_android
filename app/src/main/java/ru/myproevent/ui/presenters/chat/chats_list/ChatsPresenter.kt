package ru.myproevent.ui.presenters.chat.chats_list

import com.github.terrakok.cicerone.Router
import ru.myproevent.R
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.repositories.events.IProEventEventsRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import ru.myproevent.ui.presenters.events.adapter.IChatsListPresenter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ChatsPresenter(localRouter: Router) : BaseMvpPresenter<ChatsView>(localRouter) {

    @Inject
    lateinit var eventsRepository: IProEventEventsRepository

    inner class EventsListPresenter(
        private val itemClickListener: ((Event) -> Unit)? = null,
        private val editIconClickListener: ((Event) -> Unit)? = null
    ) : IChatsListPresenter {

        private var allEvents = listOf<Event>()
        private var events = listOf<Event>()
        private var eventFilter = Event.Status.ALL

        override fun getCount() = events.size

        override fun bindView(view: IChatItemView) {
            val event = events[view.pos]
            view.setName(event.name)
            //view.setTime(formatDate(event.startDate, event.endDate))
        }

        override fun onEditButtonClick(view: IChatItemView) {
            editIconClickListener?.invoke(events[view.pos])
        }

        override fun onItemClick(view: IChatItemView) {
            itemClickListener?.invoke(events[view.pos])
        }

        fun setData(data: List<Event>) {
            allEvents = data
            filter(eventFilter)
        }

        fun filter(status: Event.Status) {
            eventFilter = status
            events = if (status == Event.Status.ALL) allEvents
            else allEvents.filter { it.status == status }
            viewState.updateList()
        }

        private fun formatDate(start: Date, end: Date): String {
            var dateFormat: SimpleDateFormat
            val sb = StringBuilder()

            if (inSameDay(start, end)) {
                dateFormat = SimpleDateFormat("dd.MM.yyyy")
                sb.append(dateFormat.format(start)).append(" с ")

                dateFormat = SimpleDateFormat(" HH.mm")
                sb.append(dateFormat.format(start)).append(" до ").append(dateFormat.format(end))
            } else {
                dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                sb.append(dateFormat.format(start)).append(" - ").append(dateFormat.format(end))
            }

            return sb.toString()
        }

        private fun inSameDay(d1: Date, d2: Date): Boolean {
            val c1 = GregorianCalendar().apply { time = d1 }
            val c2 = GregorianCalendar().apply { time = d2 }
            val year = Calendar.YEAR
            val month = Calendar.MONTH
            val day = Calendar.DAY_OF_MONTH
            return c1[day] == c2[day] && c1[month] == c2[month] && c1[year] == c2[year]
        }
    }


    val eventsListPresenter = EventsListPresenter({
        localRouter.navigateTo(screens.event(it))
    }, {
        localRouter.navigateTo(screens.event(it))
    })

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
        loadData()
    }

    fun loadData() {
        eventsRepository.getEvents()
            .observeOn(uiScheduler)
            .subscribe({ data ->
                viewState.setNoEventsLayoutVisibility(data.isEmpty())
                eventsListPresenter.setData(data)
            }, {
                viewState.showMessage(
                    getString(
                        R.string.error_occurred,
                        it.message
                    )
                )
            }).disposeOnDestroy()
    }

    fun addEvent() {
        localRouter.navigateTo(screens.event())
    }

    fun onFilterChosen(status: Event.Status) {
        viewState.hideFilterOptions()
        viewState.selectFilterOption(status)

        eventsListPresenter.filter(status)
    }

}
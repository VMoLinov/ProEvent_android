package ru.myproevent.ui.presenters.events.event.event_screen_items_presenters

import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.adapters.event_items.EventScreenItem
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IEventDateItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IEventDateItemView
import ru.myproevent.ui.presenters.events.event.EventPresenter

class EventDateItemPresenter(
    private val eventPresenter: EventPresenter
) : IEventDateItemPresenter()  {
    private fun isDateExpired(timeInterval: TimeInterval) =
        (timeInterval.end < System.currentTimeMillis()) // TODO: отрефакторить: получать время от сущности получаемой через dagger

    override fun onItemClick(view: IEventDateItemView) {
        if (!eventPresenter.isCurrentUserOwnsEvent || isDateExpired((eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.EventDateItem).timeInterval)) {
            return
        }
        onEditClick(view)
    }

    override fun onEditClick(view: IEventDateItemView) {
        if (!eventPresenter.isCurrentUserOwnsEvent || isDateExpired((eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.EventDateItem).timeInterval)) {
            return
        }
        eventPresenter.openDatePicker((eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.EventDateItem).timeInterval)
    }

    override fun onRemoveClick(view: IEventDateItemView) {
        val date =
            (eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.EventDateItem).timeInterval
        if (!eventPresenter.isCurrentUserOwnsEvent || isDateExpired(date)) {
            return
        }
        eventPresenter.removeDate(date)
    }

    override fun bindView(view: IEventDateItemView) = with(view) {
        with((eventPresenter.eventScreenListPresenter.eventScreenItems[view.pos] as EventScreenItem.EventDateItem).timeInterval) {
            setStartDate(start)
            setEndDate(end)
            val isDateExpired = isDateExpired(this)
            setAsExpired(isDateExpired)
            setEditOption(eventPresenter.isCurrentUserOwnsEvent && !isDateExpired)
        }
    }
}
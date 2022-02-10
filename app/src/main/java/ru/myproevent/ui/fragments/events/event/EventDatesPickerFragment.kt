package ru.myproevent.ui.fragments.events.event

import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentEventDatesPickerBinding
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.datespicker.EventDatesPickerPresenter
import ru.myproevent.ui.presenters.events.event.datespicker.EventDatesPickerView
import ru.myproevent.ui.presenters.main.RouterProvider

class EventDatesPickerFragment :
    BaseMvpFragment<FragmentEventDatesPickerBinding>(FragmentEventDatesPickerBinding::inflate),
    EventDatesPickerView, BackButtonListener {

    override val presenter by moxyPresenter {
        EventDatesPickerPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance(dates: TimeInterval?) = EventDatesPickerFragment()
    }
}

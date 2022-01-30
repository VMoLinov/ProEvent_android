package ru.myproevent.ui.presenters.events

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface EventsView : BaseMvpView {
    fun init()
    fun updateList()
    fun setNoEventsLayoutVisibility(visible: Boolean)
    fun setProgressBarVisibility(visible: Boolean)
}

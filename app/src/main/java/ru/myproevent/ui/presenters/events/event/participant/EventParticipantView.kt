package ru.myproevent.ui.presenters.events.event.participant

import android.os.Bundle
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface EventParticipantView : BaseMvpView {
    @OneExecution
    fun openChat(userId: Long)

    @OneExecution
    fun setResult(requestKey: String, result: Bundle)
}
package ru.myproevent.ui.presenters.authorization.authorization

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface AuthorizationView : BaseMvpView {
    fun authorizationDataInvalid()

    @OneExecution
    fun finishAuthorization()
}
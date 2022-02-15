package ru.myproevent.ui.presenters.authorization.code

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface CodeView : BaseMvpView {
    fun showCodeErrorMessage(message: String?)
}
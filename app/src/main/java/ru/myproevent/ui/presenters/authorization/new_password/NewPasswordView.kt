package ru.myproevent.ui.presenters.authorization.new_password

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface NewPasswordView : BaseMvpView {
    fun showCodeErrorMessage(message: String?)
    fun showPasswordConfirmErrorMessage(message: String?)

    @OneExecution
    fun finishAuthorization()
}
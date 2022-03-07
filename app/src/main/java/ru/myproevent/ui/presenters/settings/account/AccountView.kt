package ru.myproevent.ui.presenters.settings.account

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface AccountView : BaseMvpView {
    @Skip
    fun showProfile(profile: Profile)
    fun makeProfileEditable()

    //@AddToEnd
    fun setFieldEdited(id: Map<Int, Boolean>)
}

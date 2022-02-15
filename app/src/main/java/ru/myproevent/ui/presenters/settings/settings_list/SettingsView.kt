package ru.myproevent.ui.presenters.settings.settings_list

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.myproevent.ui.presenters.BaseMvpView

@AddToEndSingle
interface SettingsView : BaseMvpView {
    fun logout()
}
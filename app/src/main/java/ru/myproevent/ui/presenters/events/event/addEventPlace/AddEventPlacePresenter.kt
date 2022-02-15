package ru.myproevent.ui.presenters.events.event.addEventPlace

import com.github.terrakok.cicerone.Router
import ru.myproevent.ui.presenters.BaseMvpPresenter

class AddEventPlacePresenter(localRouter: Router) :
    BaseMvpPresenter<AddEventPlaceView>(localRouter) {

    fun backTo() {
        localRouter.backTo(screens.currentlyOpenEventScreen())
    }
}
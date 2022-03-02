package ru.myproevent.ui.presenters.home

import com.github.terrakok.cicerone.Router
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

class HomePresenter(localRouter: Router) : BaseMvpPresenter<HomeView>(localRouter) {
    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    fun getId(): String = loginRepository.getLocalId().toString()

    fun getToken(): String = loginRepository.getLocalToken().toString()

    fun registerToken(token: String?) {
        token?.let {
            loginRepository.saveFirebaseToken(token)
                .observeOn(uiScheduler)
                .subscribe()
                .disposeOnDestroy()
        }
    }
}
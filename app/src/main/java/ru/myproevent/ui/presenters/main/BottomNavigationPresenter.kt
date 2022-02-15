package ru.myproevent.ui.presenters.main

import com.github.terrakok.cicerone.Router
import moxy.InjectViewState
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.ui.presenters.BaseMvpPresenter
import javax.inject.Inject

@InjectViewState
class BottomNavigationPresenter(localRouter: Router) :
    BaseMvpPresenter<BottomNavigationView>(localRouter) {
    // TODO: отрефакторить - реализовать friend инкапсуляцию в виде анотации
    private class Access
    class BottomNavigationPresenterFriendAccess private constructor() {
        constructor(access: Any) : this() {
            if (access !is Access) {
                throw Exception("Friend access violation: Переданный access не соответствует BottomNavigationPresenter.Access. Экземляр BottomNavigationPresenterFriendAccess может быть создан только BottomNavigationPresenter.")
            }
        }
    }

    private val friendAccess = BottomNavigationPresenterFriendAccess(Access())

    @Inject
    lateinit var loginRepository: IProEventLoginRepository

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (loginRepository.getLocalToken() == null) {
            viewState.showTab(Tab.AUTHORIZATION, friendAccess)
        } else {
            viewState.showTab(Tab.HOME, friendAccess)
        }
    }

    fun openTab(tab: Tab) {
        viewState.showTab(tab, friendAccess)
    }

    fun exit() {
        viewState.resetState(friendAccess)
    }

    var currFragmentTag: String? = null
}
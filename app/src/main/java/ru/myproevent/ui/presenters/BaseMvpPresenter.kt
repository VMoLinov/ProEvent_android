package ru.myproevent.ui.presenters

import android.widget.Toast
import com.github.terrakok.cicerone.Router
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import moxy.MvpPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.ui.screens.IScreens
import javax.inject.Inject

open class BaseMvpPresenter<V : BaseMvpView>(protected open var localRouter: Router) :
    MvpPresenter<V>() {

    @Inject
    lateinit var uiScheduler: Scheduler

    @Inject
    lateinit var globalRouter: Router

    @Inject
    lateinit var screens: IScreens

    private var compositeDisposable = CompositeDisposable()

    protected inner class InterAccessInfoObserver(private val onAccessErrorMessage: String?) :
        DisposableSingleObserver<Boolean>() {
        override fun onSuccess(hasInternetAccess: Boolean) {
            if (!hasInternetAccess) {
                viewState.showMessage("У устройства нет выхода в Интернет.\n(От Google Public DNS нет ответа)")
            } else {
                viewState.showMessage("Вас нашёл баг: $onAccessErrorMessage")
            }
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            Toast.makeText(ProEventApp.instance, "${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    protected fun Disposable.disposeOnDestroy() {
        compositeDisposable.add(this)
    }

    open fun onBackPressed(): Boolean {
        localRouter.exit()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
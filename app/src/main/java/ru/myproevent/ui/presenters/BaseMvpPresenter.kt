package ru.myproevent.ui.presenters

import android.widget.Toast
import com.github.terrakok.cicerone.Router
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import moxy.MvpPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.domain.models.repositories.resourceProvider.IResourceProvider
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

    @Inject
    lateinit var resourceProvider: IResourceProvider

    private var compositeDisposable = CompositeDisposable()

    protected inner class InterAccessInfoObserver(private val onAccessErrorMessage: String?) :
        DisposableSingleObserver<Boolean>() {
        override fun onSuccess(hasInternetAccess: Boolean) {
            if (!hasInternetAccess) {
                viewState.showMessage(getString(R.string.no_internet_message))
            } else {
                viewState.showMessage(getString(R.string.bug_find_you, onAccessErrorMessage))
            }
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
            Toast.makeText(ProEventApp.instance, error.message, Toast.LENGTH_LONG).show()
        }
    }

    protected fun Disposable.disposeOnDestroy() {
        compositeDisposable.add(this)
    }

    protected fun getString(id: Int) = resourceProvider.getString(id)

    protected fun getString(id: Int, vararg formatArgs: Any?) =
        resourceProvider.getString(id, *formatArgs)

    open fun onBackPressed(): Boolean {
        localRouter.exit()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
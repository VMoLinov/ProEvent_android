package ru.myproevent.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.domain.models.LocalCiceroneHolder
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.authorization.AuthorizationFragment
import ru.myproevent.ui.fragments.authorization.LoginFragment
import ru.myproevent.ui.fragments.authorization.RecoveryFragment
import ru.myproevent.ui.fragments.authorization.RegistrationFragment
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.presenters.main.Tab
import ru.myproevent.ui.screens.IScreens
import javax.inject.Inject

class TabContainerFragment : Fragment(), RouterProvider, BackButtonListener {

    private val navigator: Navigator by lazy {
        object : AppNavigator(requireActivity(), R.id.ftc_container, childFragmentManager) {
            override fun setupFragmentTransaction(
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment?
            ) {
                setVerticalTransitionAnimation(
                    currentFragment,
                    nextFragment,
                    fragmentTransaction
                )
            }
        }
    }

    private fun setVerticalTransitionAnimation(
        currFragment: Fragment?,
        nextFragment: Fragment?,
        fragmentTransaction: FragmentTransaction
    ) {
        if (currFragment == null) {
            return
        }
        when (nextFragment) {
            is AuthorizationFragment -> {
                return
            }
            is RegistrationFragment,
            is LoginFragment,
            is RecoveryFragment -> {
                fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_bottom,
                    R.anim.stay_still,
                    R.anim.stay_still,
                    R.anim.exit_to_bottom
                )
                return
            }
        }
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
    }

    @Inject
    lateinit var ciceroneHolder: LocalCiceroneHolder

    @Inject
    lateinit var screens: IScreens

    private val containerType: Tab
        get() = requireArguments().getSerializable(CONTAINER_TYPE_KEY)!! as Tab

    override fun onCreate(savedInstanceState: Bundle?) {
        ProEventApp.instance.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    private val cicerone: Cicerone<Router>
        get() = ciceroneHolder.getCicerone(containerType.name)

    companion object {
        private const val CONTAINER_TYPE_KEY = "CONTAINER_TYPE"

        fun newInstance(tab: Tab) =
            TabContainerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CONTAINER_TYPE_KEY, tab)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab_container, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (childFragmentManager.findFragmentById(R.id.ftc_container) == null) {
            when (containerType) {
                Tab.AUTHORIZATION -> {
                    cicerone.router.replaceScreen(screens.authorization())
                    Log.d("[MYLOG]", "cicerone.router.replaceScreen(screens.authorization())")
                }
                Tab.HOME -> {
                    cicerone.router.replaceScreen(screens.home())
                    Log.d("[MYLOG]", "cicerone.router.replaceScreen(screens.home())")
                }
                Tab.CONTACTS -> cicerone.router.replaceScreen(screens.contacts())
                Tab.CHAT -> cicerone.router.replaceScreen(screens.chats())
                Tab.EVENTS -> cicerone.router.replaceScreen(screens.events())
                Tab.SETTINGS -> cicerone.router.replaceScreen(screens.settings())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override val router: Router
        get() = cicerone.router

    override fun onBackPressed(): Boolean {
        val fragment = childFragmentManager.findFragmentById(R.id.ftc_container)
        return if (fragment != null && fragment is BackButtonListener
            && (fragment as BackButtonListener).onBackPressed()
        ) {
            true
        } else {
            (activity as RouterProvider?)!!.router.exit()
            true
        }
    }
}
package ru.myproevent.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentHomeBinding
import ru.myproevent.domain.firebase.FirebaseService
import ru.myproevent.ui.presenters.home.HomePresenter
import ru.myproevent.ui.presenters.home.HomeView
import ru.myproevent.ui.presenters.main.RouterProvider

class HomeFragment : BaseMvpFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), HomeView {

    override val presenter by moxyPresenter {
        HomePresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("[MYLOG]", "token: ${presenter.getToken()}")
        id.text = "ID: ${presenter.getId()}"
        token.text = "token:\n${presenter.getToken()}"
        presenter.registerToken(
            requireContext().getSharedPreferences(
                FirebaseService.SHARED_PREF_FILE,
                Context.MODE_PRIVATE
            ).getString(FirebaseService.SHARED_PREF_VALUE, null)
        )
    }
}

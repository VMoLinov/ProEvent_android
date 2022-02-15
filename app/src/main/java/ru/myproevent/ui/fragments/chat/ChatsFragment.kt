package ru.myproevent.ui.fragments.chat

import android.os.Bundle
import android.view.View
import moxy.ktx.moxyPresenter
import ru.myproevent.databinding.FragmentChatsBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.BaseMvpPresenter
import ru.myproevent.ui.presenters.BaseMvpView
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.screens.Screens


class ChatsFragment : BaseMvpFragment<FragmentChatsBinding>(FragmentChatsBinding::inflate),
    BaseMvpView {

    companion object {
        fun newInstance() = ChatsFragment()
    }

    val router by lazy { (parentFragment as RouterProvider).router }

    override val presenter by moxyPresenter {
        BaseMvpPresenter<BaseMvpView>((parentFragment as RouterProvider).router).apply {}
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            profileContainer1.setOnClickListener { router.navigateTo(Screens().chat()) }
            profileContainer2.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer3.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer4.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer5.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer6.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer7.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer8.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer9.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer10.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer11.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer12.setOnClickListener { router.navigateTo(Screens().chat1()) }
            profileContainer13.setOnClickListener { router.navigateTo(Screens().chat1()) }
        }
    }

}
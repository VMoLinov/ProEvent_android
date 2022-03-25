package ru.myproevent.ui.fragments.events.event.event_map

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentEventMapBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.event_map.EventMapPresenter
import ru.myproevent.ui.presenters.events.event.event_map.EventMapView
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.compose.EventMap
import ru.myproevent.ui.compose.theme.ProeventTheme

class EventMapFragment : BaseMvpFragment<FragmentEventMapBinding>(FragmentEventMapBinding::inflate),
    EventMapView {

    override val presenter by moxyPresenter {
        EventMapPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = EventMapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: отрефакторить: разобраться как это вынести в кастомную вьюху EventMap
        binding.mapComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ProeventTheme {
                    EventMap()
                }
            }
        }
    }
}
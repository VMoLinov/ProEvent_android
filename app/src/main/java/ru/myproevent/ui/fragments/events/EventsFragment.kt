package ru.myproevent.ui.fragments.events

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentEventsBinding
import ru.myproevent.domain.models.entities.Event.Status
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.EventsPresenter
import ru.myproevent.ui.presenters.events.EventsView
import ru.myproevent.ui.presenters.events.adapter.EventsRVAdapter
import ru.myproevent.ui.presenters.main.RouterProvider


class EventsFragment : BaseMvpFragment<FragmentEventsBinding>(FragmentEventsBinding::inflate),
    EventsView {

    companion object {
        fun newInstance() = EventsFragment()
    }

    override val presenter by moxyPresenter {
        EventsPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    var adapter: EventsRVAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHeaderFilter()
        binding.addEvent.setOnClickListener { presenter.addEvent() }
        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.appBar.bringToFront();
    }

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }

    private fun initHeaderFilter() = with(binding) {
        headerFilter.setItems(R.array.events_filter_items, R.array.events_filter_titles)
        headerFilter.setOnItemClickListener { i, _ ->
            val status = when (i) {
                0 -> Status.ALL
                1 -> Status.ACTUAL
                2 -> Status.COMPLETED
                else -> Status.ALL
            }
            presenter.onFilterChosen(status)
        }
    }

    override fun init() = with(binding) {
        rvEvents.layoutManager = LinearLayoutManager(context)
        adapter = EventsRVAdapter(presenter.eventsListPresenter)
        rvEvents.adapter = adapter
    }

    override fun updateList() {
        adapter?.notifyDataSetChanged()
    }

    override fun setNoEventsLayoutVisibility(visible: Boolean) {
        binding.noEventsLayout.visibility = if (visible) VISIBLE
        else GONE
    }

    override fun setProgressBarVisibility(visible: Boolean) {
        binding.progressBar.visibility = if (visible) VISIBLE
        else GONE
    }
}
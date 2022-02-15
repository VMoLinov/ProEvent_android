package ru.myproevent.ui.fragments.events.event

import android.os.Bundle
import android.view.View
import android.widget.Toast
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentEventActionConfirmationBinding
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.confirmation.EventActionConfirmPresenter
import ru.myproevent.ui.presenters.events.event.confirmation.EventActionConfirmView
import ru.myproevent.ui.presenters.main.RouterProvider

class EventActionConfirmationFragment :
    BaseMvpFragment<FragmentEventActionConfirmationBinding>(FragmentEventActionConfirmationBinding::inflate),
    EventActionConfirmView,
    BackButtonListener {
    private val event: Event by lazy {
        requireArguments().getParcelable(EVENT_ARG)!!
    }

    private val status: Event.Status? by lazy {
        requireArguments().getParcelable(STATUS_ARG)
    }

    override val presenter by moxyPresenter {
        EventActionConfirmPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        const val EVENT_ARG = "EVENT"
        const val STATUS_ARG = "STATUS"
        fun newInstance(event: Event, status: Event.Status?) =
            EventActionConfirmationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EVENT_ARG, event)
                    putParcelable(STATUS_ARG, status)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            title.text = event.name
            back.setOnClickListener { presenter.onBackPressed() }
            backHitArea.setOnClickListener { back.performClick() }
            when (status) {
                Event.Status.COMPLETED -> {
                    confirmTitle.text = getString(R.string.event_finish_confirm_question)
                    confirm.text = getString(R.string.finish)
                    confirm.setOnClickListener {
                        presenter.editStatus(event, Event.Status.COMPLETED)
                    }
                }

                Event.Status.CANCELLED -> {
                    confirmTitle.text = getString(R.string.event_cancel_confirm_question)
                    confirm.text = getString(R.string.event_cancel_button_text)
                    confirm.setOnClickListener {
                        presenter.editStatus(event, Event.Status.CANCELLED)
                    }
                }

                null -> {
                    confirmTitle.text = getString(R.string.event_delete_confirm_question)
                    confirm.text = getString(R.string.delete)
                    confirm.setOnClickListener {
                        presenter.deleteEvent(event)
                    }
                }
            }
            cancel.setOnClickListener { presenter.onBackPressed() }
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(ProEventApp.instance, message, Toast.LENGTH_LONG).show()
    }
}
package ru.myproevent.ui.fragments.events.event.participant_pickers

import android.os.Bundle
import android.view.View
import android.widget.Toast
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentContactAddBinding
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.participant_pickers.participant_by_email_picker.ParticipantByEmailPickerPresenter
import ru.myproevent.ui.presenters.events.event.participant_pickers.participant_by_email_picker.ParticipantByEmailPickerView
import ru.myproevent.ui.presenters.main.RouterProvider

class ParticipantByEmailPickerFragment :
    BaseMvpFragment<FragmentContactAddBinding>(FragmentContactAddBinding::inflate),
    ParticipantByEmailPickerView, BackButtonListener {
    override val presenter by moxyPresenter {
        ParticipantByEmailPickerPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = ParticipantByEmailPickerFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            contactAddExplanation.text =
                getString(R.string.participant_add_by_profile_value_explanation)
            titleButton.setOnClickListener { presenter.onBackPressed() }
            searchContact.setOnClickListener {
                presenter.inviteParticipantByEmail(emailEdit.text.toString())
            }
        }
    }

    override fun setResult(requestKey: String, result: Bundle) {
        Toast.makeText(
            requireContext(),
            "Данный функционал пока недоступен. На сервере нет функции приглашения участника по email.",
            Toast.LENGTH_LONG
        ).show()
    }
}
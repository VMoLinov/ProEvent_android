package ru.myproevent.ui.fragments.events.event.participant_pickers

import android.os.Bundle
import android.view.View
import android.widget.Toast
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentParticipantPickerTypeSelectionBinding
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.participant_pickers.ParticipantPickerTypeSelectionPresenter
import ru.myproevent.ui.presenters.events.event.participant_pickers.ParticipantPickerTypeSelectionView
import ru.myproevent.ui.presenters.main.RouterProvider

class ParticipantPickerTypeSelectionFragment :
    BaseMvpFragment<FragmentParticipantPickerTypeSelectionBinding>(
        FragmentParticipantPickerTypeSelectionBinding::inflate
    ),
    ParticipantPickerTypeSelectionView,
    BackButtonListener {
    override val presenter by moxyPresenter {
        ParticipantPickerTypeSelectionPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        const val PARTICIPANTS_IDS_ARG = "PARTICIPANTS_IDS"
        fun newInstance(participantsIds: List<Long>) =
            ParticipantPickerTypeSelectionFragment().apply {
                arguments = Bundle().apply {
                    putLongArray(
                        PARTICIPANTS_IDS_ARG,
                        participantsIds.toLongArray()
                    )
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            back.setOnClickListener { presenter.onBackPressed() }
            backHitArea.setOnClickListener { back.performClick() }
            pickFromContacts.setOnClickListener {
                presenter.pickFromContacts(
                    requireArguments().getLongArray(
                        PARTICIPANTS_IDS_ARG
                    )!!.toList()
                )
            }
            pickByEmail.setOnClickListener { presenter.pickByEmail() }
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(ProEventApp.instance, message, Toast.LENGTH_LONG).show()
    }
}
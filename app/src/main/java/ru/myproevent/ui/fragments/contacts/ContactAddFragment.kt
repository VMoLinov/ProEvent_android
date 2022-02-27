package ru.myproevent.ui.fragments.contacts

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentContactAddBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.contacts.contact_add.ContactAddPresenter
import ru.myproevent.ui.presenters.contacts.contact_add.ContactAddView
import ru.myproevent.ui.presenters.main.RouterProvider

class ContactAddFragment :
    BaseMvpFragment<FragmentContactAddBinding>(FragmentContactAddBinding::inflate), ContactAddView {

    override val presenter by moxyPresenter {
        ContactAddPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = ContactAddFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        searchContact.setOnClickListener {
            presenter.findContact(emailEdit.text.toString())
        }
        inviteContact.setOnClickListener {
            presenter.inviteContact(emailEdit.text.toString())
        }
        titleButton.setOnClickListener { presenter.onBackPressed() }
    }

    override fun showInvitationForm() = with(binding) {
        contactAddExplanation.visibility = GONE
        searchContact.visibility = GONE
        contactInviteExplanation.visibility = VISIBLE
        nameInputContainer.visibility = VISIBLE
        inviteContact.visibility = VISIBLE
    }

    override fun showSearchForm() = with(binding) {
        contactAddExplanation.visibility = VISIBLE
        searchContact.visibility = VISIBLE
        contactInviteExplanation.visibility = GONE
        nameInputContainer.visibility = GONE
        inviteContact.visibility = GONE
    }
}
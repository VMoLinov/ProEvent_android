package ru.myproevent.ui.fragments.contacts

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import moxy.MvpView
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentContactBinding
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.contacts.contact.ContactPresenter
import ru.myproevent.ui.presenters.contacts.contact.ContactView
import ru.myproevent.ui.presenters.main.RouterProvider

class ContactFragment : BaseMvpFragment<FragmentContactBinding>(FragmentContactBinding::inflate),
    ContactView {

    companion object {
        private const val BUNDLE_PROFILE_ID = "profileId"
        fun newInstance(id: Long) = ContactFragment().apply {
            arguments = Bundle().apply { putLong(BUNDLE_PROFILE_ID, id) }
        }
    }

    override val presenter by moxyPresenter {
        ContactPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleButton.setOnClickListener { presenter.onBackPressed() }
        arguments?.getLong(BUNDLE_PROFILE_ID)?.let { presenter.loadProfile(it) }
    }

    override fun setTitle(str: String) = binding.titleButton.setText(str)

    override fun setBirthDate(str: String) = with(binding) {
        dateOfBirthTitle.visibility = VISIBLE
        dateOfBirthValue.visibility = VISIBLE
        dateOfBirthValue.text = str
    }

    override fun setPosition(str: String) = with(binding) {
        positionTitle.visibility = VISIBLE
        positionValue.visibility = VISIBLE
        positionValue.text = str
    }

    override fun setPhone(str: String) = with(binding) {
        phoneTitle.visibility = VISIBLE
        phoneValue.visibility = VISIBLE
        phoneValue.text = str
    }
}
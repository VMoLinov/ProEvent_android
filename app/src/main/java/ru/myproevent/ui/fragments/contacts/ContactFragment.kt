package ru.myproevent.ui.fragments.contacts

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentContactBinding
import ru.myproevent.domain.models.entities.contact.Contact
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.BaseMvpPresenter
import ru.myproevent.ui.presenters.BaseMvpView
import ru.myproevent.ui.presenters.main.RouterProvider

class ContactFragment : BaseMvpFragment<FragmentContactBinding>(FragmentContactBinding::inflate) {

    private lateinit var contact: Contact

    companion object {
        private const val BUNDLE_CONTACT = "contact"
        fun newInstance(contact: Contact) = ContactFragment().apply {
            arguments = Bundle().apply { putParcelable(BUNDLE_CONTACT, contact) }
        }
    }

    override val presenter by moxyPresenter {
        BaseMvpPresenter<BaseMvpView>((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleButton.setOnClickListener { presenter.onBackPressed() }
        arguments?.getParcelable<Contact>(BUNDLE_CONTACT)?.let { contact = it }
        fillFields()
    }

    private fun fillFields() = with(binding) {
        with(contact) {
            titleButton.text =
                if (!fullName.isNullOrBlank()) {
                    fullName
                } else if (!nickName.isNullOrBlank()) {
                    nickName
                } else {
                    "[id: ${userId}]"
                }
            if (!birthdate.isNullOrBlank()) {
                dateOfBirthTitle.visibility = VISIBLE
                dateOfBirthValue.visibility = VISIBLE
                dateOfBirthValue.text = birthdate
            }
            if (!position.isNullOrBlank()) {
                positionTitle.visibility = VISIBLE
                positionValue.visibility = VISIBLE
                positionValue.text = position
            }
            if (!msisdn.isNullOrBlank()) {
                phoneTitle.visibility = VISIBLE
                phoneValue.visibility = VISIBLE
                phoneValue.text = msisdn
            }
        }
    }
}
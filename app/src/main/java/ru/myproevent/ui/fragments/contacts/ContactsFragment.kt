package ru.myproevent.ui.fragments.contacts

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentContactsBinding
import ru.myproevent.domain.models.entities.Contact.Status
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.contacts.contacts_list.ContactsPresenter
import ru.myproevent.ui.presenters.contacts.contacts_list.ContactsView
import ru.myproevent.ui.presenters.contacts.contacts_list.adapters.ContactsRVAdapter
import ru.myproevent.ui.presenters.main.RouterProvider

class ContactsFragment : BaseMvpFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate),
    ContactsView {

    companion object {
        fun newInstance() = ContactsFragment()
    }

    override val presenter by moxyPresenter {
        ContactsPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    var adapter: ContactsRVAdapter? = null

    private var confirmScreenCallBack: ((confirmed: Boolean) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        initHeaderFilter()
        addContact.setOnClickListener { presenter.addContact() }
        addFirstContact.setOnClickListener { presenter.addContact() }
        btnYes.setOnClickListener { confirmScreenCallBack?.invoke(true) }
        btnNo.setOnClickListener { confirmScreenCallBack?.invoke(false) }

        // https://stackoverflow.com/questions/20103888/animatelayoutchanges-does-not-work-well-with-nested-layout
        root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initHeaderFilter() = with(binding) {
        headerFilter.setItems(R.array.contacts_filter_items, R.array.contacts_filter_titles)
        headerFilter.setOnItemClickListener { i, _ ->
            val status = when (i) {
                0 -> Status.ALL
                1 -> Status.PENDING
                2 -> Status.REQUESTED
                else -> Status.ALL
            }
            presenter.filterContacts(status)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadData()
    }

    override fun init() = with(binding) {
        rvContacts.layoutManager = LinearLayoutManager(context)
        adapter = ContactsRVAdapter(presenter.contactsListPresenter)
        rvContacts.adapter = adapter
    }

    override fun hideConfirmationScreen() {
        binding.confirmScreen.visibility = GONE
    }

    override fun showConfirmationScreen(
        message: String,
        callBack: ((confirmed: Boolean) -> Unit)?
    ) {
        binding.tvConfirmMsg.text = message
        confirmScreenCallBack = callBack
        binding.confirmScreen.visibility = VISIBLE
    }

    override fun updateContactsList() {
        adapter?.notifyDataSetChanged()
    }

    override fun setProgressBarVisibility(visible: Boolean) {
        binding.progressBar.visibility = if (visible) VISIBLE
        else GONE
    }

    override fun hideNoContactsLayout() {
        binding.noContactsLayout.visibility = GONE
    }

    override fun showNoContactsLayout(status: Status) {
        binding.noContactsText.text = when (status) {
            Status.ALL -> getString(R.string.you_have_no_contacts)
            else -> getString(R.string.you_have_no_active_requests)
        }
        binding.noContactsLayout.visibility = VISIBLE
    }
}
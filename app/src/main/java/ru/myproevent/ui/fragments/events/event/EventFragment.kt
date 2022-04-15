package ru.myproevent.ui.fragments.events.event

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentEventBinding
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.domain.utils.*
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.adapters.event_items.EventScreenRVAdapter
import ru.myproevent.ui.presenters.events.event.EventPresenter
import ru.myproevent.ui.presenters.events.event.EventView
import ru.myproevent.ui.presenters.main.RouterProvider

class EventFragment : BaseMvpFragment<FragmentEventBinding>(FragmentEventBinding::inflate),
    EventView, BackButtonListener {

    companion object {
        val EVENT_ARG = "EVENT"
        fun newInstance(event: Event? = null) = EventFragment().apply {
            arguments = Bundle().apply { putParcelable(EVENT_ARG, event) }
        }
    }

    override val presenter by moxyPresenter {
        EventPresenter(
            (parentFragment as RouterProvider).router,
            arguments?.getParcelable(EVENT_ARG)
        ).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    var adapter: EventScreenRVAdapter? = null

    private var pickResultCallback: ((Uri?) -> Unit)? = null

    private val getImageResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        pickResultCallback?.invoke(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initResultListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    override fun init() = with(binding) {
        rvBody.layoutManager = LinearLayoutManager(context)
        adapter = EventScreenRVAdapter(presenter.eventScreenListPresenter)
        rvBody.adapter = adapter
    }

    override fun updateEventScreenList() {
        adapter?.notifyDataSetChanged()
    }

    override fun eventScreenListNotifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        adapter?.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun eventScreenListNotifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        adapter?.notifyItemRangeRemoved(positionStart, itemCount)
    }

    override fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm = ProEventApp.instance.getSystemService(InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus() // TODO: разобраться почему это сточка пофиксила баг. Как воспроизвести баг записал на видео
        }
    }

    override fun launchImagePicker(pickResultCallback: (Uri?) -> Unit) {
        this.pickResultCallback = pickResultCallback
        getImageResultLauncher.launch("image/*")
    }

    override fun showAbsoluteFormsHeader(
        title: String,
        editIcon: Int?,
        editIconTint: Int?,
        onCollapse: () -> Unit,
        onCollapseScrollToPosition: Int,
        onEdit: () -> Unit
    ) = with(binding.absoluteBar) {
        isExpanded = true
        visibility = View.VISIBLE
        setEditIcon(editIcon)
        setEditIconTint(editIconTint)
        onExpandClickListener = {
            binding.rvBody.scrollToPosition(onCollapseScrollToPosition)
            binding.rvBody.fling(0, 0)
            onCollapse()
        }
        onEditItemsClickListener = { onEdit() }
        this.title = title
    }

    override fun hideAbsoluteBar() = with(binding.absoluteBar) {
        visibility = View.GONE
    }

    override fun showEditOptions() = with(binding) {
        editOptions.isVisible = true
    }

    override fun hideEditOptions() = with(binding) {
        editOptions.isVisible = false
    }

    override fun enableSaveOptions() = with(binding) {
        saveEdits.setTextColor(resources.getColor(R.color.ProEvent_blue_800, null))
        cancelEdits.setTextColor(resources.getColor(R.color.ProEvent_blue_800, null))
    }

    override fun disableSaveOptions() = with(binding) {
        saveEdits.setTextColor(resources.getColor(R.color.PE_blue_gray_03, null))
        cancelEdits.setTextColor(resources.getColor(R.color.PE_blue_gray_03, null))
    }

    override fun enableActionOptions() = with(binding) {
        actionMenuIcon.isVisible = true
        overflowMenu.setOptions(presenter.getEventActionOptions())
    }

    override fun eventBarTitleSet(title: String) {
        binding.title.text = title
    }

    private fun initClickListeners() = with(binding) {
        saveEdits.setOnClickListener { presenter.eventEditOptionsPresenter.saveEvent() }
        cancelEdits.setOnClickListener { presenter.eventEditOptionsPresenter.cancelEdit() }
        actionMenuIconHitArea.setOnClickListener {
            if (actionMenuIcon.isVisible) {
                actionMenuIcon.performClick()
            }
        }
        actionMenuIcon.setOnClickListener {
            overflowMenu.isVisible = !overflowMenu.isVisible
        }
    }

    private fun initResultListeners() {
        parentFragmentManager.setFragmentResultListener(
            PARTICIPANTS_PICKER_RESULT_KEY,
            this
        ) { _, bundle ->
            val participantsContacts = bundle.getParcelableArray(CONTACTS_KEY)!! as Array<Contact>
            presenter.addParticipantsProfiles(participantsContacts.map { it }.toTypedArray())
        }

        parentFragmentManager.setFragmentResultListener(
            PARTICIPANT_TO_REMOVE_ID_RESULT_KEY,
            this
        ) { _, bundle ->
            presenter.removeParticipant(bundle.getLong(PARTICIPANT_ID_KEY))
        }

        parentFragmentManager.setFragmentResultListener(
            DATE_PICKER_ADD_RESULT_KEY,
            this
        ) { _, bundle ->
            bundle.getParcelable<TimeInterval>(NEW_DATE_KEY)?.let { presenter.addEventDate(it) }
        }

        parentFragmentManager.setFragmentResultListener(
            DATE_PICKER_EDIT_RESULT_KEY,
            this
        ) { _, bundle ->
            bundle.getParcelable<TimeInterval>(OLD_DATE_KEY)?.let { presenter.removeDate(it) }
            bundle.getParcelable<TimeInterval>(NEW_DATE_KEY)?.let { presenter.addEventDate(it) }
        }
    }
}


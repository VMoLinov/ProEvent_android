package ru.myproevent.ui.fragments.events.event

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentEventBinding
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.models.entities.Event
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.domain.utils.CONTACTS_KEY
import ru.myproevent.domain.utils.PARTICIPANTS_PICKER_RESULT_KEY
import ru.myproevent.domain.utils.PARTICIPANT_ID_KEY
import ru.myproevent.domain.utils.PARTICIPANT_TO_REMOVE_ID_RESULT_KEY
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.adapters.event_items.EventScreenRVAdapter
import ru.myproevent.ui.presenters.events.event.EventPresenter
import ru.myproevent.ui.presenters.events.event.EventView
import ru.myproevent.ui.presenters.main.RouterProvider

class EventFragmentNew : BaseMvpFragment<FragmentEventBinding>(FragmentEventBinding::inflate),
    EventView, BackButtonListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            PARTICIPANTS_PICKER_RESULT_KEY,
            this
        ) { _, bundle ->
//            binding.noParticipants.isVisible = false
//            presenter.showEditOptions()
//            binding.participantsContainer.isVisible = true
            val participantsContacts = bundle.getParcelableArray(CONTACTS_KEY)!! as Array<Contact>
            presenter.addParticipantsProfiles(participantsContacts.map { it }.toTypedArray())
        }

        parentFragmentManager.setFragmentResultListener(
            PARTICIPANT_TO_REMOVE_ID_RESULT_KEY,
            this
        ) { _, bundle ->
            presenter.removeParticipant(bundle.getLong(PARTICIPANT_ID_KEY))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            saveEdits.setOnClickListener { presenter.saveEvent() }
            cancelEdits.setOnClickListener { presenter.cancelEdit() }
            actionMenuIconHitArea.setOnClickListener {
                if (actionMenuIcon.isVisible) {
                    actionMenuIcon.performClick()
                }
            }
            actionMenuIcon.setOnClickListener {
                overflowMenu.isVisible = !overflowMenu.isVisible
                Toast.makeText(ProEventApp.instance, "show overflowmenu", Toast.LENGTH_SHORT).show()
            }
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

    companion object {
        val EVENT_ARG = "EVENT"
        fun newInstance(event: Event? = null) = EventFragmentNew().apply {
            arguments = Bundle().apply { putParcelable(EVENT_ARG, event) }
        }
    }

    var adapter: EventScreenRVAdapter? = null

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

    override fun addParticipantItemView(profile: Profile) {
        TODO("Not yet implemented")
    }

    override fun addDateItemView(timeInterval: TimeInterval, position: Int) {
        TODO("Not yet implemented")
    }

    override fun enableDescriptionEdit() {
        TODO("Not yet implemented")
    }

    override fun expandDescription() {
        TODO("Not yet implemented")
    }

    override fun expandMaps() {
        TODO("Not yet implemented")
    }

    override fun expandPoints() {
        TODO("Not yet implemented")
    }

    override fun expandParticipants() {
        TODO("Not yet implemented")
    }

    override fun expandDates() {
        TODO("Not yet implemented")
    }

    override fun clearDates() {
        TODO("Not yet implemented")
    }

    override fun clearParticipants() {
        TODO("Not yet implemented")
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

    override fun unlockNameEdit() {
        TODO("Not yet implemented")
    }

    override fun unlockLocationEdit() {
        TODO("Not yet implemented")
    }

    override fun cancelEdit() {
        TODO("Not yet implemented")
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

    override fun disableSaveOptions() = with(binding){
        saveEdits.setTextColor(resources.getColor(R.color.PE_blue_gray_03, null))
        cancelEdits.setTextColor(resources.getColor(R.color.PE_blue_gray_03, null))
    }

    override fun enableActionOptions() = with(binding) {
        actionMenuIcon.isVisible = true
        overflowMenu.setOptions(presenter.getEventActionOptions())
    }

    override fun showDateEditOptions(position: Int) {
        TODO("Not yet implemented")
    }

    override fun hideDateEditOptions() {
        TODO("Not yet implemented")
    }

    override fun lockEdit() {
        TODO("Not yet implemented")
    }

    override fun removeParticipant(id: Long, pickedParticipantsIds: List<Long>) {
        TODO("Not yet implemented")
    }

    override fun removeDate(date: TimeInterval, pickedDates: List<TimeInterval>) {
        TODO("Not yet implemented")
    }
}

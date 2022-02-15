package ru.myproevent.ui.adapters.event_items

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.ui.adapters.event_items.view_holders.*
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IEventScreenListPresenter
import ru.myproevent.ui.adapters.event_items.view_holders.ProeventFormsHeaderViewHolder
import ru.myproevent.ui.adapters.event_items.view_holders.ProeventProfilePictureFormViewHolder
import ru.myproevent.ui.adapters.event_items.view_holders.ProeventTextFormViewHolder

class EventScreenRVAdapter(val presenter: IEventScreenListPresenter) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val firstVisibleItemPositionChangeWatcher = FirstVisibleItemPositionChangeWatcher()
    private val eventScreenViewHolderFactory = EventScreenViewHolderFactory(presenter)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        firstVisibleItemPositionChangeWatcher.init(
            this,
            recyclerView,
            presenter.proeventFormsHeaderItemPresenter
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        eventScreenViewHolderFactory.create(parent, viewType)

    override fun getItemCount() = presenter.getCount()

    // TODO: рефактоинг: как это отрефакторить?
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            EventScreenItem.ItemType.PROFILE_PICTURE_FORM.ordinal -> presenter.proeventProfilePictureFormItemPresenter.bindView(
                (holder as ProeventProfilePictureFormViewHolder).apply { pos = position })
            EventScreenItem.ItemType.TEXT_FORM.ordinal -> presenter.proeventTextFormItemPresenter.bindView(
                (holder as ProeventTextFormViewHolder).apply { pos = position })
            EventScreenItem.ItemType.FORM_HEADER.ordinal -> presenter.proeventFormsHeaderItemPresenter.bindView(
                (holder as ProeventFormsHeaderViewHolder).apply { pos = position })
            EventScreenItem.ItemType.PARTICIPANT_ITEM.ordinal -> presenter.participantItemPresenter.bindView(
                (holder as ParticipantItemViewHolder).apply { pos = position })
            EventScreenItem.ItemType.EVENT_DATE_ITEM.ordinal -> presenter.eventDateItemPresenter.bindView(
                (holder as EventDateItemViewHolder).apply { pos = position })
            EventScreenItem.ItemType.NO_ITEMS_PLACEHOLDER.ordinal -> presenter.noItemsPlaceholderItemPresenter.bindView(
                (holder as NoItemsPlaceholderViewHolder).apply { pos = position })
            EventScreenItem.ItemType.TEXT_BOX.ordinal -> presenter.textBoxPresenter.bindView(
                (holder as TextBoxViewHolder).apply { pos = position })
            else -> {
                throw RuntimeException("EventScreenRVAdapter onBindViewHolder получил holder.itemViewType который не обрабатывается. В holder.itemViewType должно быть одно из знчений EventScreenViewHolderType")
            }
        }

    override fun getItemViewType(position: Int) = presenter.getType(position)

}
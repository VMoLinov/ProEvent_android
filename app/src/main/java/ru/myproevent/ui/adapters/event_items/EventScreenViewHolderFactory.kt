package ru.myproevent.ui.adapters.event_items

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.databinding.*
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IEventScreenListPresenter
import ru.myproevent.ui.adapters.event_items.view_holders.*

// TODO: отрефакторить: как это использовать?
//val itemsViewHolders = mapOf(
//    Pair(
//        EventScreenItem.ItemType.PROFILE_PICTURE_FORM.ordinal,
//        Pair(EventScreenRVAdapter.ProeventProfilePictureFormViewHolder::class, ItemProfilePictureBinding::class)
//    ),
//    Pair(
//        EventScreenItem.ItemType.TEXT_FORM.ordinal,
//        Pair(EventScreenRVAdapter.ProeventTextFormViewHolder::class, ItemProeventTextFormBinding::class)
//    ),
//    Pair(
//        EventScreenItem.ItemType.FORM_HEADER.ordinal,
//        Pair(EventScreenRVAdapter.ProeventFormsHeaderViewHolder::class, ItemProeventFormsHeaderBinding::class)
//    ),
//    Pair(
//        EventScreenItem.ItemType.PARTICIPANT_ITEM.ordinal,
//        Pair(EventScreenRVAdapter.ParticipantItemViewHolder::class, ItemContactBinding::class)
//    ),
//    Pair(
//        EventScreenItem.ItemType.EVENT_DATE_ITEM.ordinal,
//        Pair(EventScreenRVAdapter.EventDateItemViewHolder::class, ItemEventDateBinding::class)
//    )
//)

class EventScreenViewHolderFactory(
    val presenter: IEventScreenListPresenter
) {
    fun create(
        parent: android.view.ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = when (viewType) {
        EventScreenItem.ItemType.PROFILE_PICTURE_FORM.ordinal -> ProeventProfilePictureFormViewHolder(
            binding = ItemProfilePictureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            presenter = presenter.profilePictureFormItemPresenter
        )
        EventScreenItem.ItemType.TEXT_FORM.ordinal -> ProeventTextFormViewHolder(
            ItemProeventTextFormBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            presenter = presenter.textFormItemPresenter
        )
        EventScreenItem.ItemType.FORM_HEADER.ordinal -> ProeventFormsHeaderViewHolder(
            ItemProeventFormsHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            presenter = presenter.formsHeaderItemPresenter
        )
        EventScreenItem.ItemType.PARTICIPANT_ITEM.ordinal -> ParticipantItemViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            presenter = presenter.participantItemPresenter
        )
        EventScreenItem.ItemType.EVENT_DATE_ITEM.ordinal -> EventDateItemViewHolder(
            ItemEventDateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            presenter = presenter.eventDateItemPresenter
        )
        EventScreenItem.ItemType.NO_ITEMS_PLACEHOLDER.ordinal -> NoItemsPlaceholderViewHolder(
            ItemNoItemsPlaceholderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            presenter = presenter.noItemsPlaceholderItemPresenter
        )
        EventScreenItem.ItemType.TEXT_BOX.ordinal -> TextBoxViewHolder(
            ItemTextBoxBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            presenter = presenter.textBoxPresenter
        )
        else -> {
            throw RuntimeException("EventScreenRVAdapter onCreateViewHolder получил viewType который не обрабатывается. Во viewType должно быть одно из знчений EventScreenViewHolderType")
        }
    }
}

package ru.myproevent.ui.presenters.events.event.participant_pickers.participant_from_contacts_picker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.R
import ru.myproevent.databinding.ItemContactPickerBinding
import ru.myproevent.domain.models.entities.Contact
import ru.myproevent.domain.utils.load
import ru.myproevent.ui.presenters.events.event.participant_pickers.participant_from_contacts_picker.IContactPickerItemView

class ContactsPickerRVAdapter(val presenter: IContactPickerPresenter) :
    RecyclerView.Adapter<ContactsPickerRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemContactPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = presenter.getCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        presenter.bindView(holder.apply { pos = position })

    inner class ViewHolder(private val vb: ItemContactPickerBinding) :
        RecyclerView.ViewHolder(vb.root),
        IContactPickerItemView {

        init {
            itemView.setOnClickListener { presenter.onItemClick(this) }
            vb.requestStatus.setOnClickListener { (presenter.onStatusClick(this)) }
            vb.requestStatusHitArea.setOnClickListener { vb.requestStatus.performClick() }
        }

        override var pos = -1

        override fun setSelection(isSelected: Boolean) {
            vb.selection.isVisible = isSelected
        }

        override fun setName(name: String) {
            vb.tvName.text = name
        }

        override fun setDescription(description: String) {
            vb.tvDescription.text = description
        }

        override fun loadImg(url: String) {
            vb.ivImg.load(url)
        }

        override fun setStatus(status: Contact.Status) = with(vb) {
            requestStatus.setImageDrawable(
                when (status) {
                    Contact.Status.REQUESTED -> AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.ic_incomming_request
                    )
                    Contact.Status.DECLINED -> AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.ic_rejected_request
                    )
                    Contact.Status.PENDING -> AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.ic_outgoing_request
                    )
                    else -> null
                }
            )
        }
    }
}
package ru.myproevent.ui.adapters.event_items.view_holders

import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.databinding.ItemEventDateBinding
import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IEventDateItemView
import java.text.SimpleDateFormat
import java.util.*

class EventDateItemViewHolder(
    private val binding: ItemEventDateBinding,
    private val presenter: IItemPresenter<IEventDateItemView>
) :
    RecyclerView.ViewHolder(binding.root),
    IEventDateItemView {

    init {
        itemView.setOnClickListener {
            presenter.onItemClick(
                this
            )
        }
    }

    override var pos = -1

    override fun setStartDate(timestamp: Long) = with(binding) {
        title.text = getDateTime(timestamp, "dd MMMM yyyy, EEEE")
        startDateValue.text = getDateTime(timestamp, "EEE dd MMM yyyyг. HH:mm")
    }

    override fun setEndDate(timestamp: Long) = with(binding) {
        endDateValue.text = getDateTime(timestamp, "EEE dd MMM yyyyг. HH:mm")
    }

    private fun getDateTime(timeStamp: Long, pattern: String): String {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            val netDate = Date(timeStamp * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}
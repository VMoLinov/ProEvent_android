package ru.myproevent.ui.adapters.event_items.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.ItemEventDateBinding
import ru.myproevent.ui.adapters.IItemPresenter
import ru.myproevent.ui.adapters.event_items.presenter_interfaces.IEventDateItemPresenter
import ru.myproevent.ui.adapters.event_items.view_item_interfaces.IEventDateItemView
import java.text.SimpleDateFormat
import java.util.*

class EventDateItemViewHolder(
    private val binding: ItemEventDateBinding,
    private val presenter: IEventDateItemPresenter
) :
    RecyclerView.ViewHolder(binding.root),
    IEventDateItemView {

    init {
        itemView.setOnClickListener {
            presenter.onItemClick(this)
        }
        binding.editDate.setOnClickListener {
            presenter.onEditClick(this)
        }
        binding.removeDate.setOnClickListener {
            presenter.onRemoveClick(this)
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

    override fun setAsExpired(isExpired: Boolean) = with(binding) {
        if(!isExpired){
            title.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_800, null))
            startDateTitle.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_800, null))
            startDateValue.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_600, null))
            endDateTitle.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_800, null))
            endDateValue.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_600, null))
        } else {
            title.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_300, null))
            startDateTitle.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_300, null))
            startDateValue.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_300, null))
            endDateTitle.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_300, null))
            endDateValue.setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_300, null))
        }
    }

    override fun setEditOption(isEditAvailable: Boolean) = with(binding){
        if(isEditAvailable){
            editDate.visibility = View.VISIBLE
            removeDate.visibility = View.VISIBLE
        } else {
            editDate.visibility = View.INVISIBLE
            removeDate.visibility = View.INVISIBLE
        }
    }

    private fun getDateTime(timeStamp: Long, pattern: String): String {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            val netDate = Date(timeStamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}
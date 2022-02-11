package ru.myproevent.ui.fragments.events.event

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.DialogDatePickerBinding
import ru.myproevent.databinding.FragmentEventDatesPickerBinding
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.datespicker.EventDatesPickerPresenter
import ru.myproevent.ui.presenters.events.event.datespicker.EventDatesPickerView
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.views.KeyboardAwareTextInputEditText
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class EventDatesPickerFragment :
    BaseMvpFragment<FragmentEventDatesPickerBinding>(FragmentEventDatesPickerBinding::inflate),
    EventDatesPickerView, BackButtonListener {

    private var timeIntervalInput: TimeInterval? = null
    private val timeIntervalOutput: MutableList<TimeInterval> = mutableListOf()
    private val repeatTypes: Array<String> by lazy { resources.getStringArray(R.array.repeat_types) }
    override val presenter by moxyPresenter {
        EventDatesPickerPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeIntervalInput = arguments?.getParcelable(DATES_ARGS)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        initRepeatAutoCompleteTextView(binding.repeatEdit)
    }

    private fun setListeners() {
        with(binding) {
            datesBar.backHitArea.setOnClickListener { presenter.onBackPressed() }
            beginInput.setEndIconOnClickListener {
                showDialog(getString(R.string.dates_start_event), beginEdit)
            }
            endInput.setEndIconOnClickListener {
                showDialog(getString(R.string.dates_end_event), endEdit)
            }
            repeatStartInput.setEndIconOnClickListener {
                showDialog(getString(R.string.dates_start_repeat), repeatStartEdit)
            }
            repeatEndInput.setEndIconOnClickListener {
                showDialog(getString(R.string.dates_end_repeat), repeatEndEdit)
            }
            datesBar.saveHitArea.setOnClickListener {
                handleSaveArea(isVisible = false)
            }
        }
    }

    private fun initRepeatAutoCompleteTextView(repeatEdit: AutoCompleteTextView) {
        binding.repeatContainer.visibility = View.GONE
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, repeatTypes)
        with(repeatEdit) {
            setText(repeatTypes[START_INDEX])
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                val container = binding.repeatContainer
                when (position) {
                    START_INDEX -> container.visibility = View.GONE
                    else -> container.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showDialog(title: String, inputView: KeyboardAwareTextInputEditText) {
        val dialogBinding = DialogDatePickerBinding.inflate(layoutInflater)
        constraintsPickerDialog(dialogBinding)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)
            .setTitle(title)
            .setPositiveButton(getString(R.string.dates_dialog_positive)) { _, _ ->
                with(dialogBinding) {
                    val timeStamp = onDateSet(
                        datePicker.dayOfMonth,
                        datePicker.month,
                        datePicker.year,
                        timePicker.hour,
                        timePicker.minute
                    )
                    inputView.setText(convertLongToString(timeStamp))
                    handleSaveArea(isVisible = true)
                }
            }
            .setNegativeButton(getString(R.string.dates_dialog_cancel)) { _, _ -> }
            .create()
            .show()
    }

    private fun handleSaveArea(isVisible: Boolean) {
        with(binding.datesBar) {
            saveHitArea.isVisible = isVisible
            save.isVisible = isVisible
            cancelHitArea.isVisible = isVisible
            cancel.isVisible = isVisible
        }
    }

    private fun constraintsPickerDialog(dialogBinding: DialogDatePickerBinding) {
        with(dialogBinding) {
            val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
            datePicker.minDate = calendar.timeInMillis
            calendar.add(Calendar.YEAR, YEARS_TO_GO)
            datePicker.maxDate = calendar.timeInMillis
            val is24hours = android.text.format.DateFormat.is24HourFormat(requireContext())
            timePicker.setIs24HourView(is24hours)
        }
    }

    private fun convertLongToString(timeStamp: Long): String {
        val date = Date(timeStamp)
        val formatter: DateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }

    private fun onDateSet(dayOfMonth: Int, month: Int, year: Int, hour: Int, minute: Int): Long {
        val calendar: Calendar = GregorianCalendar(year, month, dayOfMonth, hour, minute)
        return calendar.timeInMillis
    }

    companion object {
        private const val START_INDEX = 0
        private const val YEARS_TO_GO = 5
        private const val DATES_ARGS = "dates arguments"
        fun newInstance(dates: TimeInterval?) = EventDatesPickerFragment().apply {
            arguments = Bundle().apply { putParcelable(DATES_ARGS, dates) }
        }
    }
}

package ru.myproevent.ui.fragments.events.event

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.children
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.CalendarDayBinding
import ru.myproevent.databinding.CalendarFragmentBinding
import ru.myproevent.databinding.CalendarHeaderBinding
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.domain.utils.DATE_PICKER_ADD_RESULT_KEY
import ru.myproevent.domain.utils.DATE_PICKER_EDIT_RESULT_KEY
import ru.myproevent.domain.utils.NEW_DATE_KEY
import ru.myproevent.domain.utils.OLD_DATE_KEY
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.activity.BottomNavigationActivity
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.datespicker.EventDatesPickerPresenter
import ru.myproevent.ui.presenters.events.event.datespicker.EventDatesPickerView
import ru.myproevent.ui.presenters.main.BottomNavigation
import ru.myproevent.ui.presenters.main.RouterProvider
import java.time.*
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class EventDatesPickerFragment :
    BaseMvpFragment<CalendarFragmentBinding>(CalendarFragmentBinding::inflate),
    EventDatesPickerView, BackButtonListener {

    companion object {
        private const val CELL_TEXT_SIZE = 15f
        private const val COLUMNS_COUNT = 7
        private const val RADIUS_RATIO = 20
        private const val YEARS_TO_GO = 5L
        private const val MONTH_TO_GO = 12L * YEARS_TO_GO
        fun newInstance(dates: TimeInterval?) = EventDatesPickerFragment().apply {
            arguments = Bundle().apply { putParcelable(OLD_DATE_KEY, dates) }
        }
    }

    override val presenter by moxyPresenter {
        EventDatesPickerPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    private var timeIntervalInput: TimeInterval? = null
    private var timeIntervalOutput: TimeInterval? = null
    private val today = LocalDate.now()
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val startBackground: GradientDrawable by
    lazy { requireContext().getDrawableCompat(R.drawable.dates_continuous_selected_bg_start) as GradientDrawable }
    private val endBackground: GradientDrawable by
    lazy { requireContext().getDrawableCompat(R.drawable.dates_continuous_selected_bg_end) as GradientDrawable }
    private val singleBackground: GradientDrawable by
    lazy { requireContext().getDrawableCompat(R.drawable.dates_single_selected_bg) as GradientDrawable }
    private val todayBackground: GradientDrawable by
    lazy { requireContext().getDrawableCompat(R.drawable.dates_today_bg) as GradientDrawable }
    private val hourSpinnerEnd: NumberPicker by lazy {
        binding.timePickerEnd.findViewById(
            Resources.getSystem().getIdentifier("hour", "id", "android")
        )
    }
    private val minuteSpinnerEnd: NumberPicker by lazy {
        binding.timePickerEnd.findViewById(
            Resources.getSystem().getIdentifier("minute", "id", "android")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<TimeInterval>(OLD_DATE_KEY)?.let { timeIntervalInput = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initDatesPosition()
        calculateCellsRadius()
        calendarSetFirstDay()
        calendarDayBinder()
        calendarMonthHeader()
        set24HourViewOnTimePickers()
        timePickersNotify()
        timePickersListeners()
        saveButtonListener()
        saveButtonCheckEnable()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.calendar_menu, menu)
        menu.findItem(R.id.menuItemDelete).icon.apply {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                resources.getColor(R.color.ProEvent_white, activity?.theme),
                BlendModeCompat.SRC_ATOP
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val closeIndicator = requireContext().getDrawableCompat(R.drawable.ic_close)?.apply {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                resources.getColor(R.color.ProEvent_white, activity?.theme),
                BlendModeCompat.SRC_ATOP
            )
        }
        (activity as BottomNavigation).hideBottomNavigation()
        (activity as BottomNavigationActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(closeIndicator)
                title = getString(R.string.dates_range_pick)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menuItemDelete -> {
                startDate = null
                endDate = null
                timePickersNotify()
                binding.calendar.notifyCalendarChanged()
                saveButtonCheckEnable()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        (activity as BottomNavigation).showBottomNavigation()
        (activity as BottomNavigationActivity).setSupportActionBar(null)
    }

    private fun initDatesPosition() {
        timeIntervalInput?.let {
            startDate = Instant.ofEpochMilli(it.start).atZone(ZoneId.systemDefault()).toLocalDate()
            endDate = Instant.ofEpochMilli(it.end).atZone(ZoneId.systemDefault()).toLocalDate()
            val start = Calendar.getInstance(Locale.getDefault())
            val end = Calendar.getInstance(Locale.getDefault())
            start.timeInMillis = it.start
            end.timeInMillis = it.end
            with(binding) {
                timePickerStart.hour = start.get(Calendar.HOUR_OF_DAY)
                timePickerStart.minute = start.get(Calendar.MINUTE)
                timePickerEnd.hour = end.get(Calendar.HOUR_OF_DAY)
                timePickerEnd.minute = end.get(Calendar.MINUTE)
            }
        }
    }

    private fun calculateCellsRadius() {
        binding.calendar.post {
            val radius = ((binding.calendar.width / COLUMNS_COUNT) / RADIUS_RATIO).toFloat()
            startBackground.setCornerRadius(topLeft = radius, bottomLeft = radius)
            endBackground.setCornerRadius(topRight = radius, bottomRight = radius)
            singleBackground.cornerRadius = radius
            todayBackground.cornerRadius = radius
        }
    }

    private fun calendarSetFirstDay() {
        // Set the First day of week depending on Locale
        val daysOfWeek = daysOfWeekFromLocale()
        binding.legendLayout.root.children.forEachIndexed { index, currentView ->
            (currentView as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, CELL_TEXT_SIZE)
            }
        }
        val currentMonth = YearMonth.now()
        binding.calendar.setup(
            currentMonth, currentMonth.plusMonths(MONTH_TO_GO), daysOfWeek.first()
        )
        binding.calendar.scrollToMonth(currentMonth)
    }

    private fun calendarDayBinder() {
        binding.calendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.dayText
                textView.text = null
                textView.background = null
                val startDate = startDate
                val endDate = endDate
                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.text = day.day.toString()
                    if (day.date.isBefore(today)) {
                        textView.setTextColorRes(R.color.ProEvent_blue_300)
                    } else {
                        when {
                            startDate == day.date && endDate == null -> {
                                textView.setTextColorRes(R.color.ProEvent_white)
                                textView.background = singleBackground
                            }
                            day.date == startDate -> {
                                textView.setTextColorRes(R.color.ProEvent_white)
                                textView.background = startBackground
                            }
                            startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                                textView.setTextColorRes(R.color.ProEvent_white)
                                textView.setBackgroundResource(R.drawable.dates_continuous_selected_bg_middle)
                            }
                            day.date == endDate -> {
                                textView.setTextColorRes(R.color.ProEvent_white)
                                textView.background = endBackground
                            }
                            day.date == today -> {
                                textView.setTextColorRes(R.color.ProEvent_blue_600)
                                textView.background = todayBackground
                            }
                            else -> textView.setTextColorRes(R.color.ProEvent_blue_600)
                        }
                    }
                }
            }
        }
    }

    private fun calendarMonthHeader() {
        binding.calendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthTitle =
                    if (month.year == today.year) {
                        resources.getStringArray(R.array.dates_months)[month.month]
                    } else {
                        "${resources.getStringArray(R.array.dates_months)[month.month]} ${month.year}"
                    }
                container.textView.text = monthTitle
            }
        }
    }

    private fun set24HourViewOnTimePickers() {
        with(binding) {
            timePickerStart.setIs24HourView(true)
            timePickerEnd.setIs24HourView(true)
        }
    }

    private fun timePickersNotify() {
        with(binding) {
            minuteSpinnerEnd.minValue = 0
            hourSpinnerEnd.minValue = if (endDate == null) {
                if (hourSpinnerEnd.value <= timePickerEnd.hour) {
                    minuteSpinnerEnd.minValue = timePickerStart.minute
                }
                timePickerStart.hour
            } else 0
        }
    }

    private fun timePickersListeners() {
        with(binding) {
            timePickerStart.setOnTimeChangedListener { _, startHour, startMinute ->
                if (endDate == null) {
                    hourSpinnerEnd.minValue = startHour
                    minuteSpinnerEnd.minValue =
                        if (hourSpinnerEnd.value == startHour) startMinute else 0
                }
            }
            timePickerEnd.setOnTimeChangedListener { _, endHour, _ ->
                minuteSpinnerEnd.minValue =
                    if (endHour == timePickerStart.hour) timePickerStart.minute else 0
            }
        }
    }

    private fun saveButtonListener() {
        binding.saveButton.setOnClickListener {
            collectData()
            timeIntervalInput?.let {
                parentFragmentManager.setFragmentResult(
                    DATE_PICKER_EDIT_RESULT_KEY,
                    Bundle().apply {
                        putParcelable(NEW_DATE_KEY, timeIntervalOutput)
                        putParcelable(OLD_DATE_KEY, it)
                    })
            }
            if (timeIntervalInput == null) {
                timeIntervalOutput?.let {
                    parentFragmentManager.setFragmentResult(
                        DATE_PICKER_ADD_RESULT_KEY,
                        Bundle().apply {
                            putParcelable(NEW_DATE_KEY, it)
                        })
                }
            }
            onBackPressed()
        }
    }

    private fun collectData() {
        val endDate = endDate
        val startTime: Long?
        val endTime: Long?
        startDate?.let {
            startTime = GregorianCalendar(
                it.year,
                it.monthValue - 1,
                it.dayOfMonth,
                binding.timePickerStart.hour,
                binding.timePickerStart.minute
            ).timeInMillis
            if (endDate != null) {
                endTime = GregorianCalendar(
                    endDate.year,
                    endDate.monthValue - 1,
                    endDate.dayOfMonth,
                    binding.timePickerEnd.hour,
                    binding.timePickerEnd.minute
                ).timeInMillis
            } else {
                endTime = GregorianCalendar(
                    it.year,
                    it.monthValue - 1,
                    it.dayOfMonth,
                    binding.timePickerEnd.hour,
                    binding.timePickerEnd.minute
                ).timeInMillis
            }
            timeIntervalOutput = TimeInterval(startTime, endTime)
        }
    }

    private fun saveButtonCheckEnable() {
        // Enable save button if a day is selected or no date is selected.
        binding.saveButton.isEnabled = startDate != null
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay // Will be set when this container is bound.
        val binding = CalendarDayBinding.bind(view)

        init {
            view.setOnClickListener {
                if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(
                        today
                    ))
                ) {
                    val date = day.date
                    if (startDate != null) {
                        if (date < startDate || endDate != null) {
                            startDate = date
                            endDate = null
                            timePickersNotify()
                        } else if (date != startDate) {
                            endDate = date
                            timePickersNotify()
                        }
                    } else {
                        startDate = date
                    }
                    this@EventDatesPickerFragment.binding.calendar.notifyCalendarChanged()
                    saveButtonCheckEnable()
                }
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarHeaderBinding.bind(view).headerText
    }

    private fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
        ContextCompat.getDrawable(this, drawable)

    private fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

    private fun TextView.setTextColorRes(@ColorRes color: Int) =
        setTextColor(context.getColorCompat(color))

    private fun GradientDrawable.setCornerRadius(
        topLeft: Float = 0F,
        topRight: Float = 0F,
        bottomRight: Float = 0F,
        bottomLeft: Float = 0F
    ) {
        cornerRadii = arrayOf(
            topLeft, topLeft,
            topRight, topRight,
            bottomRight, bottomRight,
            bottomLeft, bottomLeft
        ).toFloatArray()
    }

    private fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }
}

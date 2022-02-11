package ru.myproevent.ui.fragments.events.event

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import ru.myproevent.R
import ru.myproevent.databinding.CalendarDayBinding
import ru.myproevent.databinding.CalendarFragmentBinding
import ru.myproevent.databinding.CalendarHeaderBinding
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.domain.utils.NEW_DATE_KEY
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.activity.BottomNavigationActivity
import ru.myproevent.ui.presenters.main.BottomNavigation
import ru.myproevent.ui.presenters.main.RouterProvider
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class EventDatesPickerFragment : Fragment(), BackButtonListener {

    private var _binding: CalendarFragmentBinding? = null
    private val binding get() = _binding!!
    private val timeIntervalOutput: MutableList<TimeInterval> = mutableListOf()
    private val today = LocalDate.now()
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private val router by lazy { (parentFragment as RouterProvider).router }
    private val startBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.example_4_continuous_selected_bg_start) as GradientDrawable
    }

    private val endBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.example_4_continuous_selected_bg_end) as GradientDrawable
    }
    private val singleBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.example_4_single_selected_bg) as GradientDrawable
    }
    private val todayBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.example_4_today_bg) as GradientDrawable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<TimeInterval>(NEW_DATE_KEY)?.let {
            timeIntervalOutput.add(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.calendar_menu, menu)
        menu.getItem(menu.size() - 1).icon.apply {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                resources.getColor(R.color.ProEvent_white, activity?.theme),
                BlendModeCompat.SRC_ATOP
            )
        }
        menu.findItem(R.id.menuItemDelete).setOnMenuItemClickListener {
            startDate = null
            endDate = null
            binding.exFourCalendar.notifyCalendarChanged()
            saveButtonEnabled()
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calculateCellsRadius()
        calendarSetFirstDay()
        binding.timePickerStart.setIs24HourView(true)
        binding.timePickerEnd.setIs24HourView(true)
        calendarDayBinder()
        calendarMonthHeader()
    }

    private fun calendarMonthHeader() {
        binding.exFourCalendar.monthHeaderBinder = object :
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

    private fun calendarDayBinder() {
        binding.exFourCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exFourDayText
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
                                textView.setBackgroundResource(R.drawable.example_4_continuous_selected_bg_middle)
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

    private fun calculateCellsRadius() {
        binding.exFourCalendar.post {
            val radius = ((binding.exFourCalendar.width / COLUMNS_COUNT) / RADIUS_RATIO).toFloat()
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
        binding.exFourCalendar.setup(
            currentMonth, currentMonth.plusMonths(MONTH_TO_GO), daysOfWeek.first()
        )
        binding.exFourCalendar.scrollToMonth(currentMonth)
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
            setSupportActionBar(binding.exFourToolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(closeIndicator)
                title = getString(R.string.dates_range_pick)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (activity as BottomNavigation).showBottomNavigation()
        (activity as BottomNavigationActivity).setSupportActionBar(null)
    }

    override fun onBackPressed(): Boolean {
        router.exit()
        return true
    }


//    private fun constraintsPickerDialog(dialogBinding: DialogDatePickerBinding) {
//        with(dialogBinding) {
//            val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
////            datePicker.minDate = calendar.timeInMillis
//            calendar.add(Calendar.YEAR, YEARS_TO_GO)
////            datePicker.maxDate = calendar.timeInMillis
//            val is24hours = android.text.format.DateFormat.is24HourFormat(requireContext())
////            timePicker.setIs24HourView(is24hours)
//        }
//    private fun convertLongToString(timeStamp: Long): String {
//        val date = Date(timeStamp)
//        val formatter: DateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
//        formatter.timeZone = TimeZone.getTimeZone("UTC")
//        return formatter.format(date)
//    private fun onDateSet(dayOfMonth: Int, month: Int, year: Int, hour: Int, minute: Int): Long {
//        val calendar: Calendar = GregorianCalendar(year, month, dayOfMonth, hour, minute)
//        return calendar.timeInMillis

    private fun saveButtonEnabled() {
        // Enable save button if a day is selected or no date is selected.
        binding.exFourSaveButton.isEnabled = startDate != null
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
                        } else if (date != startDate) {
                            endDate = date
                        }
                    } else {
                        startDate = date
                    }
                    this@EventDatesPickerFragment.binding.exFourCalendar.notifyCalendarChanged()
                    saveButtonEnabled()
                }
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarHeaderBinding.bind(view).exFourHeaderText
    }

    companion object {
        private const val CELL_TEXT_SIZE = 15f
        private const val COLUMNS_COUNT = 7
        private const val RADIUS_RATIO = 20
        private const val YEARS_TO_GO = 5L
        private const val MONTH_TO_GO = 12L * YEARS_TO_GO
        fun newInstance(dates: TimeInterval?) = EventDatesPickerFragment().apply {
            arguments = Bundle().apply { putParcelable(NEW_DATE_KEY, dates) }
        }
    }

    private fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
        ContextCompat.getDrawable(this, drawable)

    private fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

    private fun TextView.setTextColorRes(@ColorRes color: Int) =
        setTextColor(context.getColorCompat(color))

    private fun View.makeVisible() {
        visibility = View.VISIBLE
    }

    private fun View.makeInVisible() {
        visibility = View.INVISIBLE
    }

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

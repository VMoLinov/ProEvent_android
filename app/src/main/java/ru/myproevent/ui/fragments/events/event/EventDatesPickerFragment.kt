package ru.myproevent.ui.fragments.events.event

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import ru.myproevent.databinding.DialogDatePickerBinding
import ru.myproevent.domain.models.entities.TimeInterval
import ru.myproevent.ui.activity.BottomNavigationActivity
import ru.myproevent.ui.presenters.main.BottomNavigation
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*


class EventDatesPickerFragment : Fragment() {

    private var _binding: CalendarFragmentBinding? = null
    private val binding get() = _binding!!

    private var timeIntervalInput: TimeInterval? = null
    private val timeIntervalOutput: MutableList<TimeInterval> = mutableListOf()

    @SuppressLint("NewApi")
    private val today = LocalDate.now()

    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    @SuppressLint("NewApi")
    private val headerDateFormatter = DateTimeFormatter.ofPattern("EEE'\n'd MMM")

    private val startBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.example_4_continuous_selected_bg_start) as GradientDrawable
    }

    private val endBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.example_4_continuous_selected_bg_end) as GradientDrawable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BottomNavigationActivity).setTheme(R.style.Theme_Proevent)
        timeIntervalInput = arguments?.getParcelable(DATES_ARGS)
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onStart() {
        super.onStart()
        val closeIndicator = requireContext().getDrawableCompat(R.drawable.ic_close)?.apply {
            setColorFilter(
                requireContext().getColorCompat(android.R.color.darker_gray),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        (activity as BottomNavigationActivity).hideBottomNavigation()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(closeIndicator)
        requireActivity().window.apply {
            // Update status bar color to match toolbar color.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBarColor = requireContext().getColorCompat(R.color.ProEvent_white)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                statusBarColor = Color.GRAY
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as BottomNavigation).showBottomNavigation()
        (activity as BottomNavigationActivity).setTheme(R.style.Theme_Proevent_NoActionBar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.calendar_menu, menu)
        binding.exFourToolbar.post {
            // Configure menu text to match what is in the Airbnb app.
            binding.exFourToolbar.findViewById<TextView>(R.id.menuItemDelete).apply {
                setTextColor(requireContext().getColorCompat(android.R.color.darker_gray))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                isAllCaps = false
            }
        }
        menu.findItem(R.id.menuItemDelete).setOnMenuItemClickListener {
            startDate = null
            endDate = null
            binding.exFourCalendar.notifyCalendarChanged()
            bindSummaryViews()
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exFourCalendar.post {
            val radius = ((binding.exFourCalendar.width / 7) / 20).toFloat()
            startBackground.setCornerRadius(topLeft = radius, bottomLeft = radius)
            endBackground.setCornerRadius(topRight = radius, bottomRight = radius)
        }
        // Set the First day of week depending on Locale
        val daysOfWeek = daysOfWeekFromLocale()
        binding.legendLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTextColorRes(android.R.color.darker_gray)
            }
        }

        val currentMonth = YearMonth.now()
        binding.exFourCalendar.setup(currentMonth, currentMonth.plusMonths(12), daysOfWeek.first())
        binding.exFourCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
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
                        bindSummaryViews()
                    }
                }
            }
        }
        binding.timePickerStart.setIs24HourView(true)
        binding.timePickerEnd.setIs24HourView(true)
        binding.exFourCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exFourDayText
                val roundBgView = container.binding.exFourRoundBgView
                textView.text = null
                textView.background = null
                roundBgView.makeInVisible()
                val startDate = startDate
                val endDate = endDate
                when (day.owner) {
                    DayOwner.THIS_MONTH -> {
                        textView.text = day.day.toString()
                        if (day.date.isBefore(today)) {
                            textView.setTextColorRes(android.R.color.darker_gray)
                        } else {
                            when {
                                startDate == day.date && endDate == null -> {
                                    textView.setTextColorRes(R.color.ProEvent_white)
                                    roundBgView.makeVisible()
                                    roundBgView.setBackgroundResource(R.drawable.example_4_single_selected_bg)
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
                                    textView.setTextColorRes(android.R.color.darker_gray)
                                    roundBgView.makeVisible()
                                    roundBgView.setBackgroundResource(R.drawable.example_4_today_bg)
                                }
                                else -> textView.setTextColorRes(android.R.color.darker_gray)
                            }
                        }
                    }
                }
            }
        }
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = CalendarHeaderBinding.bind(view).exFourHeaderText
        }
        binding.exFourCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)

            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthTitle =
                    "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
                container.textView.text = monthTitle
            }
        }
    }

    private fun constraintsPickerDialog(dialogBinding: DialogDatePickerBinding) {
        with(dialogBinding) {
            val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
//            datePicker.minDate = calendar.timeInMillis
            calendar.add(Calendar.YEAR, YEARS_TO_GO)
//            datePicker.maxDate = calendar.timeInMillis
            val is24hours = android.text.format.DateFormat.is24HourFormat(requireContext())
//            timePicker.setIs24HourView(is24hours)
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

    @SuppressLint("NewApi")
    private fun bindSummaryViews() {
        binding.exFourStartDateText.apply {
            if (startDate != null) {
                text = headerDateFormatter.format(startDate)
                setTextColorRes(android.R.color.darker_gray)
            } else {
                text = getString(R.string.dates_start)
                setTextColor(Color.GRAY)
            }
        }

        binding.exFourEndDateText.apply {
            if (endDate != null) {
                text = headerDateFormatter.format(endDate)
                setTextColorRes(android.R.color.darker_gray)
            } else {
                text = getString(R.string.dates_end)
                setTextColor(Color.GRAY)
            }
        }

        // Enable save button if a range is selected or no date is selected at all, Airbnb style.
        binding.exFourSaveButton.isEnabled =
            endDate != null || (startDate == null && endDate == null)
    }

    companion object {
        private const val START_INDEX = 0
        private const val YEARS_TO_GO = 5
        private const val DATES_ARGS = "dates arguments"
        fun newInstance(dates: TimeInterval?) = EventDatesPickerFragment().apply {
            arguments = Bundle().apply { putParcelable(DATES_ARGS, dates) }
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

    @SuppressLint("NewApi")
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

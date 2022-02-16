package ru.myproevent.ui.fragments.events.event

import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.KeyListener
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputLayout
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.DialogDateEditOptionsBinding
import ru.myproevent.databinding.FragmentEventBinding
import ru.myproevent.databinding.ItemContactBinding
import ru.myproevent.databinding.ItemEventDateBinding
import ru.myproevent.domain.models.entities.*
import ru.myproevent.domain.utils.*
import ru.myproevent.ui.BackButtonListener
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.fragments.ProEventMessageDialog
import ru.myproevent.ui.presenters.events.event.EventPresenter
import ru.myproevent.ui.presenters.events.event.EventView
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.views.CenteredImageSpan
import ru.myproevent.ui.views.CropImageHandler
import ru.myproevent.ui.views.KeyboardAwareTextInputEditText
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

// TODO: отрефакторить - разбить этот божественный класс на кастомные вьющки и утилиты
class EventFragment : BaseMvpFragment<FragmentEventBinding>(FragmentEventBinding::inflate),
    EventView, BackButtonListener {
    private var isFilterOptionsExpanded = false
    private var event: Event? = null
    private var address: Address? = null
    private val imageLoader = GlideLoader().apply { ProEventApp.instance.appComponent.inject(this) }

    // TODO: копирует поле licenceTouchListener из RegistrationFragment
    private val filterOptionTouchListener = View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> with(v as TextView) {
                setBackgroundColor(ProEventApp.instance.getColor(R.color.ProEvent_blue_600))
                setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_white))
            }
            MotionEvent.ACTION_UP -> with(v as TextView) {
                setBackgroundColor(ProEventApp.instance.getColor(R.color.ProEvent_white))
                setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_blue_800))
                performClick()
            }
        }
        true
    }

    private fun showFilterOptions() {
        Log.d("[MYLOG]", "eventStatus: ${event!!.status}")
        isFilterOptionsExpanded = true
        with(binding) {
            //searchEdit.hideKeyBoard() // TODO: нужно вынести это в вызов предществующий данному, чтобы тень при скрытии клавиатуры отображалась корректно
            shadow.visibility = VISIBLE
            eventBar.copy.visibility = VISIBLE
            if (event!!.ownerUserId == presenter.loginRepository.getLocalId()) {
                if (event!!.status != Event.Status.CANCELLED && event!!.status != Event.Status.COMPLETED) {
                    // TODO: появляется только если прошла последняя дата проведения, данные об этом получать с сервера
                    // finishEvent.visibility = VISIBLE
                    eventBar.cancelBar.visibility = VISIBLE
                } else {
                    eventBar.deleteBar.visibility = VISIBLE
                }
            }
        }
    }

    private fun hideFilterOptions() {
        isFilterOptionsExpanded = false
        with(binding) {
            shadow.visibility = GONE
            eventBar.copy.visibility = GONE
            eventBar.finish.visibility = GONE
            eventBar.cancelBar.visibility = GONE
            eventBar.deleteBar.visibility = GONE
        }
    }

    private var statusBarHeight by Delegates.notNull<Int>()

    private var descriptionBarDistance by Delegates.notNull<Int>()
    private var mapsBarDistance by Delegates.notNull<Int>()
    private var pointsBarDistance by Delegates.notNull<Int>()
    private var participantsBarDistance by Delegates.notNull<Int>()
    private var datesBarDistance by Delegates.notNull<Int>()

    private fun extractStatusBarHeight(): Int {
        val rectangle = Rect()
        val window: Window = requireActivity().window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.top
    }

    override val presenter by moxyPresenter {
        EventPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        const val EVENT_ARG = "EVENT"
        fun newInstance(event: Event? = null) = EventFragment().apply {
            arguments = Bundle().apply { putParcelable(EVENT_ARG, event) }
        }
    }

    private fun calculateRectOnScreen(view: View): RectF {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return RectF(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + view.measuredWidth).toFloat(),
            (location[1] + view.measuredHeight).toFloat()
        )
    }

    override fun showAbsoluteBar(
        title: String,
        iconResource: Int?,
        iconTintResource: Int?,
        onCollapseScroll: Int,
        onCollapse: () -> Unit,
        onEdit: () -> Unit
    ) =
        with(binding) {
            Log.d("[bar]", "showAbsoluteBar")

            absoluteBar.visibility = VISIBLE
            absoluteBarHitArea.visibility = VISIBLE
            absoluteBarEdit.visibility = VISIBLE
            absoluteBarExpand.visibility = VISIBLE

            iconResource?.let {
                absoluteBarEdit.visibility = VISIBLE
                absoluteBarEdit.setImageResource(it)
            } ?: run {
                absoluteBarEdit.visibility = GONE
            }
            if (iconTintResource == null) {
                absoluteBarEdit.clearColorFilter()
            } else {
                absoluteBarEdit.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        iconTintResource
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            absoluteBar.setOnClickListener { absoluteBarExpand.performClick() }
            absoluteBarHitArea.setOnClickListener { absoluteBar.performClick() }
            absoluteBarEdit.setOnClickListener { onEdit() }
            absoluteBarExpand.setOnClickListener {
                binding.scroll.scrollTo(0, onCollapseScroll)
                binding.scroll.fling(0)
                isAbsoluteBarBarHidden = true
                onCollapse()
                presenter.hideAbsoluteBar()
            }
            absoluteBar.text = title

            isAbsoluteBarBarHidden = false
        }

    private var isAbsoluteBarBarHidden = true

    override fun hideAbsoluteBar() = with(binding) {
        Log.d("[bar]", "hideAbsoluteBar")

        absoluteBar.visibility = GONE
        absoluteBarHitArea.visibility = GONE
        absoluteBarEdit.visibility = GONE
        absoluteBarExpand.visibility = GONE

        isAbsoluteBarBarHidden = true
    }

    // TODO: отрефакторить так чтобы showEditOptions вызывался только из presenter-a
    override fun showEditOptions() = with(binding.eventBar) {
        save.visibility = VISIBLE
        saveHitArea.visibility = VISIBLE
        cancel.visibility = VISIBLE
        cancelHitArea.visibility = VISIBLE
    }

    override fun hideEditOptions() = with(binding.eventBar) {
        save.visibility = GONE
        saveHitArea.visibility = GONE
        cancel.visibility = GONE
        cancelHitArea.visibility = GONE
    }

    override fun lockEdit() = with(binding) {
        lockEdit(nameInput, nameEdit)
        lockEdit(
            locationInput, locationEdit,
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.outline_place_24
            )!!
        ) {
            presenter.addEventPlace(event?.address ?: address)
        }
        nameInput.setEndIconOnClickListener {
            presenter.unlockNameEdit()
            nameEdit.requestFocus()
            showKeyBoard(nameEdit)
        }
        locationInput.setEndIconOnClickListener {
            presenter.unlockLocationEdit()
            locationEdit.requestFocus()
            showKeyBoard(locationEdit)
        }
    }


    override fun removeDate(date: TimeInterval, pickedDates: List<TimeInterval>) = with(binding) {
        pickedDates.indexOf(date).let {
            if (it == -1) {
                return@with
            }
            datesContainer.removeViewAt(it + 1)
            if (pickedDates.size == 1) {
                noDates.isVisible = true
            }
        }
    }

    override fun removeParticipant(id: Long, pickedParticipantsIds: List<Long>) = with(binding) {
        Log.d("[REMOVE]", "removeParticipant id($id) pickedParticipantsIds($pickedParticipantsIds)")
        pickedParticipantsIds.indexOf(id).let {
            if (it == -1) {
                return@with
            }
            participantsContainer.removeViewAt(it + 1)
            if (pickedParticipantsIds.size == 1) {
                noParticipants.isVisible = true
            }
        }
    }

    override fun showActionOptions() = with(binding.eventBar) {
        actionMenu.visibility = VISIBLE
    }

    private var dateEditOptionsDialogView: DialogDateEditOptionsBinding? = null

    override fun showDateEditOptions(position: Int) {
        dateEditOptionsDialogView = DialogDateEditOptionsBinding.inflate(layoutInflater)
        with(dateEditOptionsDialogView!!) {
            dateEditOptions.layoutParams =
                (dateEditOptions.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    val dateEditOptionsPosition = IntArray(2)
                    with(binding.datesContainer.getChildAt(position + 1)) {
                        getLocationOnScreen(dateEditOptionsPosition)
                        dateEditOptionsPosition[1] += height / 2
                    }
                    rightMargin = pxValue(20f).toInt()
                    topMargin = dateEditOptionsPosition[1]
                }
            background.setOnClickListener { presenter.hideDateEditOptions() }
            editDate.setOnClickListener {
                presenter.editDate(position)
                presenter.hideDateEditOptions()
                presenter.datePickerFragment(presenter.pickedDates[position])
            }
            removeDate.setOnClickListener {
                presenter.removeDate(position)
                presenter.hideDateEditOptions()
            }
        }

        dateEditOptionsDialogView!!.dateEditOptions.run {
            val dateEditOptionsPosition = IntArray(2)
            with(binding.datesContainer.getChildAt(position + 1)) {
                getLocationOnScreen(dateEditOptionsPosition)
                dateEditOptionsPosition[1] += height / 2
            }
            if (dateEditOptionsPosition[1] + dateEditOptionsDialogView!!.dateEditOptions.height > binding.rootContainer.height) {
                dateEditOptionsPosition[1] -= dateEditOptionsPosition[1] + dateEditOptionsDialogView!!.dateEditOptions.height - binding.rootContainer.height
            }
            dateEditOptionsDialogView!!.dateEditOptions.layoutParams =
                (dateEditOptionsDialogView!!.dateEditOptions.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    rightMargin = pxValue(20f).toInt()
                    topMargin = dateEditOptionsPosition[1]
                }
        }
        binding.rootContainer.addView(dateEditOptionsDialogView!!.root)
    }

    override fun hideDateEditOptions() {
        binding.rootContainer.removeView(dateEditOptionsDialogView!!.root)
        dateEditOptionsDialogView = null
    }


    private lateinit var defaultKeyListener: KeyListener

    private fun showKeyBoard(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private val uiHandler = Handler(Looper.getMainLooper())

    // https://stackoverflow.com/a/7784904/11883985
    private fun showKeyBoardByDelayedTouch(editText: EditText) {
        uiHandler.postDelayed({
            val x = editText.width.toFloat()
            val y = editText.height.toFloat()

            editText.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_DOWN,
                    x,
                    y,
                    0
                )
            )
            editText.dispatchTouchEvent(
                MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_UP,
                    x,
                    y,
                    0
                )
            )
        }, 200)
    }

    // TODO: отрефакторить - эта функция копирует функцию из AccountFragment. Вынести это в кастомную вьюху ProEventEditText
    private fun setEditListeners(
        textInput: TextInputLayout,
        textEdit: KeyboardAwareTextInputEditText,
        pickerIcon: Drawable? = null,
        pickerAction: (() -> Unit)? = null
    ) {
        textEdit.keyListener = null
    }

    private fun unlockEdit(
        textInput: TextInputLayout,
        textEdit: KeyboardAwareTextInputEditText,
        pickerIcon: Drawable? = null,
        pickerAction: (() -> Unit)? = null
    ) {
        textEdit.keyListener = defaultKeyListener
        textEdit.text?.let { it1 -> textEdit.setSelection(it1.length) }
        pickerIcon?.let {
            textInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
            textInput.endIconDrawable = it
        } ?: run {
            textInput.endIconMode = TextInputLayout.END_ICON_NONE
        }
        pickerAction?.let { textInput.setEndIconOnClickListener { pickerAction() } }
        showEditOptions()
    }

    override fun unlockNameEdit() = with(binding) {
        unlockEdit(nameInput, nameEdit)
    }

    override fun unlockLocationEdit() = with(binding) {
        unlockEdit(
            locationInput, locationEdit,
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.outline_place_24
            )!!
        ) {
            presenter.addEventPlace(event?.address ?: address)
        }
    }

    override fun cancelEdit(): Unit = with(binding) {
        if (event == null) {
            eventBar.back.performClick()
        } else {
            noDates.isVisible = true
            noParticipants.isVisible = true
            presenter.clearDates()
            presenter.clearParticipants()
            setViewValues(event!!)
            lockDescriptionEdit()
        }
    }

    private fun lockEdit(
        textInput: TextInputLayout,
        textEdit: KeyboardAwareTextInputEditText,
        pickerIcon: Drawable? = null,
        pickerAction: (() -> Unit)? = null
    ) {
        textEdit.clearFocus()
        textEdit.hideKeyBoard()
        textInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
        textInput.endIconDrawable =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_edit)!!
        setEditListeners(textInput, textEdit, pickerIcon, pickerAction)
    }

    override fun enableDescriptionEdit(): Unit = with(binding) {
        showEditOptions()
        editDescription.visibility = GONE
        absoluteBarEdit.visibility = GONE
        descriptionText.keyListener = defaultKeyListener
        noDescription.visibility = GONE
        descriptionText.visibility = VISIBLE
        descriptionText.text?.let { text -> descriptionText.setSelection(text.length) }
    }

    override fun expandDescription(): Unit = with(binding) {
        if (!isDescriptionExpanded()) {
            expandDescriptionContent()
        } else {
            expandDescription.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_blue_800
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            descriptionContainer.visibility = GONE
        }
    }

    override fun expandMaps() = with(binding) {
        fun isMapsExpanded() = mapsContainer.visibility == VISIBLE
        if (!isMapsExpanded()) {
            expandMaps.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_bright_orange_300
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            mapsContainer.visibility = VISIBLE
        } else {
            expandMaps.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_blue_800
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            mapsContainer.visibility = GONE
        }
    }

    override fun expandPoints() = with(binding) {
        fun isPointsExpanded() = pointsContainer.visibility == VISIBLE
        if (!isPointsExpanded()) {
            expandPoints.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_bright_orange_300
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            pointsContainer.visibility = VISIBLE
        } else {
            expandPoints.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_blue_800
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            pointsContainer.visibility = GONE
        }
    }

    override fun expandParticipants() = with(binding) {
        fun isParticipantsExpanded() = participantsContainer.visibility == VISIBLE
        if (!isParticipantsExpanded()) {
            expandParticipants.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_bright_orange_300
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            participantsContainer.visibility = VISIBLE
        } else {
            expandParticipants.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_blue_800
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            participantsContainer.visibility = GONE
        }
    }

    override fun expandDates() = with(binding) {
        fun isDatesExpanded() = datesContainer.visibility == VISIBLE
        if (!isDatesExpanded()) {
            expandDates.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_bright_orange_300
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            datesContainer.visibility = VISIBLE
        } else {
            expandDates.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_blue_800
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            datesContainer.visibility = GONE
        }
    }

    private fun lockDescriptionEdit() = with(binding) {
        fun showAbsoluteBarEdit() {
            isAbsoluteBarBarHidden = true
            scroll.scrollBy(0, 1)
            scroll.scrollBy(0, -1)
        }
        descriptionText.keyListener = null
        descriptionText.clearFocus()
        descriptionText.hideKeyBoard()
        editDescription.visibility = VISIBLE
        showAbsoluteBarEdit()
        if (descriptionText.text.isNullOrBlank()) {
            noDescription.visibility = VISIBLE
        }
    }

    private fun setViewValues(event: Event) = with(binding) {
        with(event) {
            nameEdit.text = SpannableStringBuilder(name)
            this.imageFile?.let {
                imageLoader.loadCircle(eventImageView, it)
            }
            address?.let { locationEdit.text = SpannableStringBuilder(it.addressLine) }
                ?: this@EventFragment.address?.let {
                    locationEdit.text = SpannableStringBuilder(it.addressLine)
                }
            if (!description.isNullOrBlank()) {
                descriptionText.text = SpannableStringBuilder(description)
                noDescription.visibility = GONE
                descriptionText.visibility = VISIBLE
            } else {
                descriptionText.text = SpannableStringBuilder("")
                noDescription.visibility = VISIBLE
                descriptionText.visibility = GONE
            }
            if (participantsUserIds != null && participantsUserIds!!.isNotEmpty()) {
                Log.d("[VIEWSTATE]", "setViewValues presenter.initParticipantsProfiles")
                binding.noParticipants.isVisible = false
                presenter.initParticipantsProfiles(participantsUserIds!!)
            }
            if (startDate != null) {
                binding.noDates.isVisible = false
                presenter.initDates(
                    listOf(
                        TimeInterval(1643977614, 1643977614 + 3600)
//                        TimeInterval(5, 5),
//                        TimeInterval(2, 2),
//                        TimeInterval(1, 1),
//                        TimeInterval(3, 3),
//                        TimeInterval(4, 4),
//                        TimeInterval(6, 6)
                    )
                )
            }
        }
    }

    override fun clearDates() = with(binding) {
        if (datesContainer.childCount > 1) {
            datesContainer.removeViews(1, datesContainer.childCount - 1)
        }
    }

    override fun clearParticipants() = with(binding) {
        Log.d("[REMOVE]", "clearParticipants")
        if (participantsContainer.childCount > 1) {
            participantsContainer.removeViews(1, participantsContainer.childCount - 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(
            DATE_PICKER_ADD_RESULT_KEY,
            this
        ) { _, bundle ->
            binding.noDates.isVisible = false
            presenter.showEditOptions()
            binding.datesContainer.isVisible = true
            bundle.getParcelable<TimeInterval>(NEW_DATE_KEY)?.let { presenter.addEventDate(it) }
        }

        parentFragmentManager.setFragmentResultListener(
            DATE_PICKER_EDIT_RESULT_KEY,
            this
        ) { _, bundle ->
            binding.noDates.isVisible = false
            presenter.showEditOptions()
            binding.datesContainer.isVisible = true
            val new: TimeInterval? = bundle.getParcelable<TimeInterval>(NEW_DATE_KEY)
            val old: TimeInterval? = bundle.getParcelable<TimeInterval>(OLD_DATE_KEY)
            presenter.removeDate(old!!)
            presenter.addEventDate(new!!)
        }

        parentFragmentManager.setFragmentResultListener(
            PARTICIPANTS_PICKER_RESULT_KEY,
            this
        ) { _, bundle ->
            binding.noParticipants.isVisible = false
            presenter.showEditOptions()
            binding.participantsContainer.isVisible = true
            val participantsContacts = bundle.getParcelableArray(CONTACTS_KEY)!! as Array<Contact>
            presenter.addParticipantsProfiles(participantsContacts.map { it }.toTypedArray())
        }

        parentFragmentManager.setFragmentResultListener(
            PARTICIPANT_TO_REMOVE_ID_RESULT_KEY,
            this
        ) { _, bundle ->
            presenter.showEditOptions()
            presenter.removeParticipant(bundle.getLong(PARTICIPANT_ID_KEY))
        }

        parentFragmentManager.setFragmentResultListener(
            AddEventPlaceFragment.ADD_EVENT_PLACE_REQUEST_KEY,
            this
        ) { _, bundle ->
            val address =
                bundle.getParcelable<Address>(AddEventPlaceFragment.ADD_EVENT_PLACE_RESULT)
            event?.let { it.address = address } ?: run { this.address = address }
            if (address != null) binding.locationEdit.setText(address.addressLine)
            presenter.showEditOptions()
        }

        arguments?.getParcelable<Event>(EVENT_ARG)?.let { event = it }
    }

    private var isSaveAvailable = true

    override fun addParticipantItemView(profile: Profile) = with(profile) {
        Log.d("[REMOVE]", "addParticipantItemView profileDto id($id)")
        val view = ItemContactBinding.inflate(layoutInflater)

        view.tvName.text = getString(
            R.string.participant_ItemView_name_template, id, when {
                !fullName.isNullOrBlank() -> fullName
                !nickName.isNullOrBlank() -> nickName
                else -> ""
            }
        )

        view.tvDescription.text = description
        view.root.setOnClickListener {
            presenter.openParticipant(this)
        }
        binding.participantsContainer.addView(view.root)
        binding.noParticipants.isVisible = false
    }

    private fun getDateTime(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd.MM (E) HH:mm", Locale.getDefault())
            val netDate = Date(timestamp)
            sdf.format(netDate).uppercase()
        } catch (e: Exception) {
            e.toString()
        }
    }

    override fun addDateItemView(timeInterval: TimeInterval, position: Int) {
        val view = ItemEventDateBinding.inflate(layoutInflater)
        view.editDate.setOnClickListener { presenter.openDateEditOptions(timeInterval) }
        val startDate = getDateTime(timeInterval.start)
        val endDate = getDateTime(timeInterval.end)
        view.dateValue.text = getString(R.string.event_date_template, startDate, endDate)
        binding.datesContainer.addView(view.root, position + 1)
        binding.noDates.isVisible = false
    }


    private fun setImageSpan(view: TextView, text: String, iconRes: Int) {
        val span: Spannable = SpannableString(text)
        val image = CenteredImageSpan(
            requireContext(),
            iconRes
        )
        span.setSpan(image, 21, 22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        view.text = span
    }

    private fun saveImageCallback(uuid: String?) {
        if (event != null) {
            event?.let {
                it.imageFile = uuid
                presenter.editEvent(it)
            }
        } else {
            addEvent(uuid.orEmpty())
        }
    }

    private fun saveCallback(successEvent: Event?) {
        isSaveAvailable = true
        binding.eventBar.save.setTextColor(resources.getColor(R.color.ProEvent_bright_orange_500))
        if (successEvent == null) {
            return
        }
        event = successEvent
        binding.eventBar.title.text = successEvent.name
        presenter.cancelEdit()
        presenter.lockEdit()
        presenter.hideEditOptions()
    }

    private fun isDescriptionExpanded() = binding.descriptionContainer.visibility == VISIBLE

    private fun expandDescriptionContent() = with(binding) {
        expandDescription.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.ProEvent_bright_orange_300
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        descriptionContainer.visibility = VISIBLE
    }

    private fun initImageCrop() {
        CropImageHandler(
            viewOnClick = binding.editEventImage,
            pickImageCallback = { pickImageActivityContract, cropActivityResultLauncher ->
                registerForActivityResult(pickImageActivityContract) {
                    it?.let { uri -> cropActivityResultLauncher.launch(uri) }
                }
            },
            cropCallback = { cropActivityContract ->
                registerForActivityResult(cropActivityContract) {
                    it?.let { uri ->
                        imageLoader.loadCircle(binding.eventImageView, uri)
                        newPictureUri(uri)
                    }
                }
            },
            isCircle = true
        ).init()
    }

    private fun addEvent(uuid: String?) {
        presenter.addEvent(
            binding.nameEdit.text.toString(),
            Calendar.getInstance().time,
            Calendar.getInstance().time,
            address,
            binding.descriptionText.text.toString(),
            uuid,
            ::saveCallback
        )
    }

    private fun newPictureUri(uri: Uri) {
        event?.imageFile?.let { presenter.deleteImage(it) }
        presenter.saveImage(File(uri.path.orEmpty()), ::saveImageCallback)
    }

    override fun onViewCreated(view: View, saved: Bundle?) {
        Log.d("[EventFragment]", "onViewCreated")
        super.onViewCreated(view, saved)
        statusBarHeight = extractStatusBarHeight()
        initImageCrop()
        with(binding) {
            event?.let {
                eventBar.title.text = it.name
            }
            if (event == null) {
                eventBar.title.text = resources.getString(R.string.event_new_event)
            }
            eventBar.title.setOnClickListener {
                // TODO: отрефакторить
                // https://github.com/terrakok/Cicerone/issues/106
                val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
                val prev: Fragment? = parentFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val newFragment: DialogFragment =
                    ProEventMessageDialog.newInstance(eventBar.title.text.toString())
                newFragment.show(ft, "dialog")
            }
            eventBar.actionMenu.setOnClickListener {
                if (!isFilterOptionsExpanded) {
                    showFilterOptions()
                } else {
                    hideFilterOptions()
                }
            }
            eventBar.actionMenuHitArea.setOnClickListener {
                if (eventBar.actionMenu.isVisible) {
                    eventBar.actionMenu.performClick()
                }
            }
            shadow.setOnClickListener { hideFilterOptions() }
            expandDescription.setOnClickListener {
                presenter.expandDescription()
                if (isDescriptionExpanded()) {
                    scroll.post {
                        scroll.smoothScrollTo(0, descriptionBarDistance)
                    }
                }
            }
            descriptionBar.setOnClickListener { expandDescription.performClick() }
            descriptionBarHitArea.setOnClickListener { descriptionBar.performClick() }
            expandMaps.setOnClickListener {
                presenter.expandMaps()
                if (mapsContainer.isVisible) {
                    scroll.post {
                        scroll.smoothScrollTo(0, mapsBarDistance)
                    }
                }
            }
            mapsBar.setOnClickListener { expandMaps.performClick() }
            mapBarHitArea.setOnClickListener { mapsBar.performClick() }
            expandPoints.setOnClickListener {
                presenter.expandPoints()
                if (pointsContainer.isVisible) {
                    scroll.post {
                        scroll.smoothScrollTo(0, pointsBarDistance)
                    }
                }
            }
            pointsBar.setOnClickListener { expandPoints.performClick() }
            pointsBarHitArea.setOnClickListener { pointsBar.performClick() }
            expandParticipants.setOnClickListener {
                presenter.expandParticipants()
                if (participantsContainer.isVisible) {
                    scroll.post {
                        scroll.smoothScrollTo(0, participantsBarDistance)
                    }
                }
            }
            participantsBar.setOnClickListener { expandParticipants.performClick() }
            participantsBarHitArea.setOnClickListener { participantsBar.performClick() }
            expandDates.setOnClickListener {
                presenter.expandDates()
                if (datesContainer.isVisible) {
                    scroll.post {
                        scroll.smoothScrollTo(0, datesBarDistance)
                    }
                }
            }
            datesBar.setOnClickListener { expandDates.performClick() }
            datesBarHitArea.setOnClickListener { datesBar.performClick() }
            addDate.setOnClickListener { presenter.datePickerFragment(null) }

            scroll.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                Log.d("[MYLOG]", "setOnScrollChangeListener")
                if (datesContainer.visibility == VISIBLE && scrollY in datesBarDistance..(datesBarDistance + datesContainer.height)) {
                    if (isAbsoluteBarBarHidden) {
                        presenter.showAbsoluteBar(
                            getString(R.string.event_dates),
                            R.drawable.ic_add,
                            null,
                            datesBarDistance,
                            { expandDates.performClick() },
                            { addDate.performClick() }
                        )
                    }
                } else if (descriptionContainer.visibility == VISIBLE && scrollY in descriptionBarDistance..(descriptionBarDistance + descriptionContainer.height)) {
                    if (isAbsoluteBarBarHidden) {
                        presenter.showAbsoluteBar(
                            getString(R.string.description),
                            if (editDescription.visibility == VISIBLE) {
                                R.drawable.ic_edit
                            } else {
                                null
                            },
                            R.color.ProEvent_blue_800,
                            descriptionBarDistance,
                            { expandDescription.performClick() },
                            { editDescription.performClick() }
                        )
                    }
                } else if (mapsContainer.visibility == VISIBLE && scrollY in mapsBarDistance..(mapsBarDistance + mapsContainer.height)) {
                    if (isAbsoluteBarBarHidden) {
                        presenter.showAbsoluteBar(
                            getString(R.string.event_map),
                            R.drawable.ic_add,
                            null,
                            mapsBarDistance,
                            { expandMaps.performClick() },
                            { addMap.performClick() }
                        )
                    }
                } else if (pointsContainer.visibility == VISIBLE && scrollY in pointsBarDistance..(pointsBarDistance + pointsContainer.height)) {
                    if (isAbsoluteBarBarHidden) {
                        presenter.showAbsoluteBar(
                            getString(R.string.points),
                            R.drawable.ic_add,
                            null,
                            pointsBarDistance,
                            { expandPoints.performClick() },
                            { addPoint.performClick() }
                        )
                    }
                } else if (participantsContainer.visibility == VISIBLE && scrollY in participantsBarDistance..(participantsBarDistance + participantsContainer.height)) {
                    if (isAbsoluteBarBarHidden) {
                        presenter.showAbsoluteBar(
                            getString(R.string.participants),
                            R.drawable.ic_add,
                            null,
                            participantsBarDistance,
                            { expandParticipants.performClick() },
                            { addParticipant.performClick() }
                        )
                    }
                } else if (!isAbsoluteBarBarHidden) {
                    presenter.hideAbsoluteBar()
                }
            }
            eventBar.save.setOnClickListener {
                if (!isSaveAvailable) {
                    return@setOnClickListener
                }
                isSaveAvailable = false
                eventBar.save.setTextColor(resources.getColor(R.color.PE_blue_gray_03))
                val editedEvent = event?.copy()
                editedEvent?.let { it ->
                    it.name = nameEdit.text.toString()
                    it.startDate = Calendar.getInstance().time
                    it.endDate = Calendar.getInstance().time
                    it.description = descriptionText.text.toString()
                    presenter.editEvent(it, ::saveCallback)
                } ?: run { addEvent(null) }
            }
            eventBar.saveHitArea.setOnClickListener { eventBar.save.performClick() }
            defaultKeyListener = nameEdit.keyListener
            if (event != null) {
                setViewValues(event!!)
                lockEdit(nameInput, nameEdit)
                nameInput.setEndIconOnClickListener {
                    presenter.unlockNameEdit()
                    nameEdit.requestFocus()
                    showKeyBoard(nameEdit)
                }
                nameEdit.addTextChangedListener {
                    eventBar.title.text = it
                }
                lockEdit(
                    locationInput, locationEdit,
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.outline_place_24
                    )!!
                ) {
                    presenter.addEventPlace(event?.address ?: address)
                }
                locationInput.setEndIconOnClickListener {
                    presenter.unlockLocationEdit()
                    locationEdit.requestFocus()
                    showKeyBoard(locationEdit)
                }
                lockDescriptionEdit()
                showActionOptions()
            } else {
                showEditOptions()
                unlockLocationEdit()
            }
            eventBar.back.setOnClickListener { presenter.onBackPressed() }
            eventBar.backHitArea.setOnClickListener { eventBar.back.performClick() }
            eventBar.cancel.setOnClickListener {
                presenter.cancelEdit()
                if (event != null) {
                    presenter.lockEdit()
                    presenter.showMessage("Изменения отменены")
                    presenter.hideEditOptions()
                }
            }
            eventBar.cancelHitArea.setOnClickListener { eventBar.cancel.performClick() }
            eventBar.copy.setOnTouchListener(filterOptionTouchListener)
            eventBar.copy.setOnClickListener {
                event?.let {
                    presenter.copyEvent(it)
                }
                hideFilterOptions()
            }
            eventBar.finish.setOnTouchListener(filterOptionTouchListener)
            eventBar.finish.setOnClickListener {
                presenter.finishEvent(event!!)
                hideFilterOptions()
            }
            eventBar.cancelBar.setOnTouchListener(filterOptionTouchListener)
            eventBar.cancelBar.setOnClickListener {
                presenter.cancelEvent(event!!)
                hideFilterOptions()
            }
            eventBar.deleteBar.setOnTouchListener(filterOptionTouchListener)
            eventBar.deleteBar.setOnClickListener {
                presenter.deleteEvent(event!!)
                hideFilterOptions()
            }
            // TODO: отрефакорить нужно передавать tint, а не использовать отдельный drawable
            setImageSpan(noDates, getString(R.string.event_fragment_some_text), R.drawable.ic_add)
            setImageSpan(
                noDescription,
                getString(R.string.event_fragment_some_text_02),
                R.drawable.ic_edit_blue
            )
            setImageSpan(noMaps, getString(R.string.event_fragment_some_text), R.drawable.ic_add)
            setImageSpan(noPoints, getString(R.string.event_fragment_some_text), R.drawable.ic_add)
            setImageSpan(
                noParticipants,
                getString(R.string.event_fragment_some_text),
                R.drawable.ic_add
            )
            editDescription.setOnClickListener {
                if (!isDescriptionExpanded()) {
                    expandDescription.performClick()
                }
                presenter.enableDescriptionEdit()
                descriptionText.requestFocus()
                //  TODO: почему-то если использовать функцию showKeyBoard вместо showKeyBoardByDelayedTouch, то
                //               при нажатии на editDescription не станет отображаться absoluteBar(но если после убрать клавиатуру, то absoluteBar появится)
                //               Я не понял почему это происходит, но хочу потом разобраться
                //showKeyBoard(descriptionText)
                showKeyBoardByDelayedTouch(descriptionText)
            }
            addMap.setOnClickListener { showMessage("addMap\nДанная возможность пока не доступна") }
            addPoint.setOnClickListener { showMessage("addPoint\nДанная возможность пока не доступна") }
            addParticipant.setOnClickListener { presenter.pickParticipants() }
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                } else {
//                    view.viewTreeObserver.removeGlobalOnLayoutListener(this)
//                }

                //Log.d("[EventFragment]", "onGlobalLayout _vb: $_vb")

                // TODO: здесь вместо viewBinding используется view.findViewById, потому что, если использовать viewBinding, то по непонятоной мне причине если
                //              выйти их этого экрана (нажав кнопку back) и вновь его открыть, то _vb будет null - несмотря на то, что в конце onCreateView он имел значение.
                //              Этого не происходит если предварительно вызвать view.viewTreeObserver.removeOnGlobalLayoutListener(this), но тогда также по непонятной мне прчине
                //              calculateRectOnScreen возращает занчения не соответствующие значениям на конечной view,
                //              то есть растояния barDistances не соответствуют тем, что отображаются на экране
                //              Желательно разобраться почему это происходит и использовать здесь viewBinding

                descriptionBarDistance =
                    (calculateRectOnScreen(view.findViewById(R.id.description_bar)).top - calculateRectOnScreen(
                        view.findViewById(R.id.scroll_child)
                    ).top).toInt()
                mapsBarDistance =
                    (calculateRectOnScreen(view.findViewById(R.id.maps_bar)).top - calculateRectOnScreen(
                        view.findViewById(R.id.scroll_child)
                    ).top).toInt()
                pointsBarDistance =
                    (calculateRectOnScreen(view.findViewById(R.id.points_bar)).top - calculateRectOnScreen(
                        view.findViewById(R.id.scroll_child)
                    ).top).toInt()
                participantsBarDistance =
                    (calculateRectOnScreen(view.findViewById(R.id.participants_bar)).top - calculateRectOnScreen(
                        view.findViewById(R.id.scroll_child)
                    ).top).toInt()
                datesBarDistance =
                    (calculateRectOnScreen(view.findViewById(R.id.dates_bar)).top - calculateRectOnScreen(
                        view.findViewById(R.id.scroll_child)
                    ).top).toInt()
            }
        })
    }

    override fun showMessage(message: String) {
        Toast.makeText(ProEventApp.instance, message, Toast.LENGTH_LONG).show()
    }
}

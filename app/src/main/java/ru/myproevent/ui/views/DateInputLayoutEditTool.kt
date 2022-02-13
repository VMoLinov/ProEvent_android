package ru.myproevent.ui.views

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.domain.utils.GlideLoader
import ru.myproevent.ui.fragments.settings.ProEventDatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class DateInputLayoutEditTool:TextInputLayoutEditTool {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val calendar: Calendar = Calendar.getInstance()
    var currYear: Int = calendar.get(Calendar.YEAR)
    var currMonth: Int = calendar.get(Calendar.MONTH)
    var currDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    private var dateEditClickListener: OnClickListener? = null
    var firstTime=true

    override fun setRedacting(){
        textEdit?.isFocusableInTouchMode = false
        textEdit?.requestFocus()
        textEdit?.setOnClickListener(dateEditClickListener)
        if(firstTime || endIconDrawable?.isVisible?:false) textEdit?.performClick()
        firstTime=false
        textEdit?.visibility = VISIBLE
        endIconMode = END_ICON_NONE
    }

    fun setDialogDate(parentFragmentManager:FragmentManager):View.OnClickListener{
         val dateEditClickListener = View.OnClickListener {
            // TODO: отрефакторить
            // https://github.com/terrakok/Cicerone/issues/106

            val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
            val prev: Fragment? = parentFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            var pickerYear = currYear
            var pickerMonth = currMonth
            var pickerDay = currDay
            if (!textEdit?.text.isNullOrEmpty()) {
                val pickerDate = GregorianCalendar().apply {
                    time =
                        SimpleDateFormat(context.getString(R.string.dateFormat)).parse(textEdit?.text.toString())
                }
                pickerYear = pickerDate.get(Calendar.YEAR)
                pickerMonth = pickerDate.get(Calendar.MONTH)
                pickerDay = pickerDate.get(Calendar.DATE)
            }
            val newFragment: DialogFragment =
                ProEventDatePickerDialog.newInstance(pickerYear, pickerMonth, pickerDay).apply {
                    onDateSetListener = { year, month, dayOfMonth ->
                        val gregorianCalendar = GregorianCalendar(
                            year, month, dayOfMonth
                        )
                        editText?.text = SpannableStringBuilder(
                            // TODO: для вывода сделать local date format
                            SimpleDateFormat(getString(R.string.dateFormat)).apply {
                                calendar = gregorianCalendar
                            }.format(
                                gregorianCalendar.time
                            )
                        )

                    }
                }
            newFragment.show(ft, "dialog")
             endIconMode = END_ICON_NONE
        }
        this.dateEditClickListener=dateEditClickListener
        return dateEditClickListener
    }

     override fun setRedacting(isEdited:Boolean):TextInputLayoutEditTool{
        if(isEdited) {
                setRedacting()
        }else {
            firstTime=true
            setEditListeners(textEdit!!, actOnClick!!)
        }
        return this
    }
}
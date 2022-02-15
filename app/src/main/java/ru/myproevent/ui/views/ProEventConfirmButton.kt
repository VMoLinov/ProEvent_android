package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.button.MaterialButton
import ru.myproevent.ProEventApp
import ru.myproevent.R


class ProEventConfirmButton : MaterialButton {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        com.google.android.material.R.attr.materialButtonStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_bright_orange_500))
            MotionEvent.ACTION_UP ->
                setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_white))
        }

        return super.dispatchTouchEvent(event)
    }
}
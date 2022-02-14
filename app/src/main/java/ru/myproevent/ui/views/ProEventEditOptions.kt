package ru.myproevent.ui.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.domain.utils.pxValue

class ProEventEditOptions : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var cancelViewParams: ViewGroup.LayoutParams
    private lateinit var saveViewParams: ViewGroup.LayoutParams

    // TODO: отрефакторить: возможно стоит создавать childViews не в самой ProEventEditOptions, а определить их в виде xml layout-а и надувать его отсюда
    private val cancelView = TextView(context).apply {
        text = "Отменить"
        // TODO: отрефакторить: передавать это как стиль через AttributeSet
        setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_800, null))
        textSize = 20F
        typeface = Typeface.createFromAsset(
            ProEventApp.instance.assets,
            ProEventApp.instance.getString(R.string.default_font_bold_path)
        )
    }

    private val saveView = TextView(context).apply {
        text = "Сохранить"
        // TODO: отрефакторить: передавать это как стиль через AttributeSet
        setTextColor(ProEventApp.instance.resources.getColor(R.color.ProEvent_blue_800, null))
        textSize = 20F
        typeface = Typeface.createFromAsset(
            ProEventApp.instance.assets,
            ProEventApp.instance.getString(R.string.default_font_bold_path)
        )
    }

    private fun updateContent() {
        removeAllViews()
        addView(cancelView, cancelViewParams)
        addView(saveView, saveViewParams)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        cancelViewParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(
                pxValue(20f).toInt(),
                0,
                0,
                0
            )
        }
        saveViewParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(
                0,
                0,
                pxValue(20f).toInt(),
                0
            )
            gravity = Gravity.RIGHT
        }
        updateContent()
    }
}
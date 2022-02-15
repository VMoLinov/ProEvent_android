package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import ru.myproevent.ProEventApp
import ru.myproevent.R

class ProEventCheckbox : FrameLayout {
    private lateinit var params: ViewGroup.LayoutParams
    private val backgroundView = ImageView(context).apply {
        setImageDrawable(ProEventApp.instance.getDrawable(R.drawable.checkbox_background))
    }
    private val borderView = ImageView(context).apply {
        setImageDrawable(ProEventApp.instance.getDrawable(R.drawable.checkbox_border))
    }
    private val foregroundView = ImageView(context).apply {
        setImageDrawable(ProEventApp.instance.getDrawable(R.drawable.checkbox_foreground))
    }

    private fun changeCheckState() {
        isChecked = !isChecked
        updateContent()
    }

    private fun updateContent() {
        removeAllViews()
        addView(backgroundView, params)
        addView(borderView, params)
        if (isChecked) {
            addView(foregroundView, params)
        }
        invalidate()
    }

    var isChecked = true
        set(value) {
            field = value
            updateContent()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun touch() {
        changeCheckState()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        params = LayoutParams(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        updateContent()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            changeCheckState()
        }
        return super.dispatchTouchEvent(event)
    }
}
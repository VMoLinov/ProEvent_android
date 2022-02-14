package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import ru.myproevent.ProEventApp
import ru.myproevent.R

class ProEventEditOptions : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var params: ViewGroup.LayoutParams

    private val foregroundView = ImageView(context).apply {
        setImageDrawable(ProEventApp.instance.getDrawable(R.drawable.checkbox_foreground))
    }

    private fun updateContent() {
        removeAllViews()
        addView(foregroundView, params)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        params = LayoutParams(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        updateContent()
    }
}
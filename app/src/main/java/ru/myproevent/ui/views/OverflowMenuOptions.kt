package ru.myproevent.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.ViewOverflowEventOptionsBinding

class OverflowMenuOptions : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(binding.root)
    }

    @SuppressLint("ClickableViewAccessibility")
    private val binding =
        ViewOverflowEventOptionsBinding.inflate(LayoutInflater.from(context)).apply {
            shadow.setOnClickListener {
                isVisible = false
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    fun setOptions(options: List<Pair<String, () -> Unit>>) {
        options.forEach { optionParam ->
            binding.options.addView(FrameLayout(context).apply {
                inflate(context, R.layout.item_overflow_option, this)
                // TODO: отрефакторить: избавиться от findViewById
                this.findViewById<TextView>(R.id.option).text = optionParam.first
                setOnTouchListener { v, event -> filterOptionTouchListener(v, event)  }
                setOnClickListener {
                    optionParam.second()
                    this@OverflowMenuOptions.isVisible = false
                }
            })
        }
    }

    // TODO: копирует поле licenceTouchListener из RegistrationFragment
    private fun filterOptionTouchListener(v: View, event: MotionEvent): Boolean {
        Toast.makeText(ProEventApp.instance, "filterOptionTouchListener", Toast.LENGTH_LONG).show()
        when (event.action) {
            MotionEvent.ACTION_UP -> with(v as FrameLayout) {
                // TODO: отрефакторить: избавиться от findViewById
                with(findViewById<TextView>(R.id.option)){
                    setBackgroundColor(ProEventApp.instance.getColor(R.color.ProEvent_white))
                    setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_blue_800))
                }
                performClick()
            }
            MotionEvent.ACTION_DOWN -> with(v as FrameLayout) {
                // TODO: отрефакторить: избавиться от findViewById
                with(findViewById<TextView>(R.id.option)){
                    setBackgroundColor(ProEventApp.instance.getColor(R.color.ProEvent_blue_600))
                    setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_white))
                }
            }
        }
        return true
    }
}
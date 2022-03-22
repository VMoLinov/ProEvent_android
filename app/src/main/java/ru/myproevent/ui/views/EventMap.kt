package ru.myproevent.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.myproevent.databinding.ViewEventMapBinding

class EventMap : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        addView(binding.root)
    }

    val binding by lazy {
        ViewEventMapBinding.inflate(LayoutInflater.from(context)).apply {
//            mapComposeView.apply {
//                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//                setContent {
//                    ProeventTheme {
//                        EventMap()
//                    }
//                }
//            }
        }
    }
}
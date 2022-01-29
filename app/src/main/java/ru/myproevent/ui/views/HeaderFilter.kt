package ru.myproevent.ui.views

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.withStyledAttributes
import ru.myproevent.R
import kotlin.math.roundToInt

class HeaderFilter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val dp = resources.displayMetrics.density
    private val sp = resources.displayMetrics.scaledDensity

    private var titleTextSize = 20 * sp
    private var titleTextColor = Color.parseColor("#2D2D59")
    private var titleTypeFace = Typeface.DEFAULT_BOLD

    private var buttonMarginStart = (12 * dp).roundToInt()
    private var buttonSrc = android.R.drawable.arrow_down_float
    private var buttonColor = Color.parseColor("#2D2D59")
    private var buttonOnClickColor = Color.parseColor("#FE3F19")

    private var itemTextSize = 16 * sp
    private var itemPaddingHorizontal = (13 * dp).roundToInt()
    private var itemPaddingVertical = (14 * dp).roundToInt()
    private var itemTextColor = Color.parseColor("#2D2D59")
    private var itemOnClickTextColor = Color.parseColor("#FFFFFFFF")
    private var itemBackgroundColor = Color.parseColor("#FFFFFFFF")
    private var itemOnClickBackgroundColor = Color.parseColor("#5C5C99")

    private var menuMarginTop = (8 * dp).roundToInt()

    //TODO: private var endIconSrc = R.drawable.ic_expand

    private var items: List<TextView> = emptyList()
    private var itemsTexts: List<String> = emptyList()
    private var titleTexts: List<String> = emptyList()

    private val title = TextView(context).apply {
        includeFontPadding = false
        textAlignment = TEXT_ALIGNMENT_CENTER
        text = "Header"
        setOnClickListener { onExpandButtonClick() }
    }

    private val expandButton = ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER
        setOnClickListener { onExpandButtonClick() }
    }

    private val titleArea = LinearLayoutCompat(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        addView(title)
        addView(expandButton)
        setOnClickListener { onExpandButtonClick() }
        this@HeaderFilter.addView(this)
    }

    private var shadowViewId: Int = NO_ID
    private val shadowView: View?
        get() = rootView.findViewById(shadowViewId)

    var expanded = false
        private set

    private var selectedItemIndex = 0

    private var userOnItemClickListener: ((index: Int, itemText: String) -> Unit)? = null
    //TODO: private var endIconClickListener: (() -> Unit)? = null

    private fun onExpandButtonClick() = expandOrCollapseMenu()

    private fun onItemTouch(index: Int, event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (index == selectedItemIndex) return
                selectItem(index)
            }
            MotionEvent.ACTION_UP -> onItemClick(index)
            else -> Unit
        }
    }

    private fun onItemClick(index: Int) {
        collapseMenu()
        if (index == selectedItemIndex) return
        title.text = titleTexts[index]
        selectedItemIndex = index

        userOnItemClickListener?.let { it(index, itemsTexts[index]) }
    }

    init {
        elevation = 2 * dp
        orientation = VERTICAL
        layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
        }
        if (attrs != null) {
            initStyle(attrs, defStyleAttr)
        }
        applyStyle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initShadowView()
    }

    private fun initShadowView() {
        shadowView?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                collapseMenu()
            }
            true
        }
    }

    fun setItems(@ArrayRes items: Int, @ArrayRes headerTexts: Int) {
        setItems(resources.getStringArray(items).toList(), resources.getStringArray(headerTexts).toList())
    }

    fun setItems(items: List<String>, headerTexts: List<String> = items) {
        if (items.isEmpty()) throw IllegalArgumentException("items shouldn't be empty")
        if (headerTexts.isEmpty()) throw IllegalArgumentException("header texts shouldn't be empty")
        if (items.size != headerTexts.size) throw IllegalArgumentException("The size of items & header texts must be the same")

        if (childCount > 1) {
            removeViews(1, childCount - 1)
        }
        itemsTexts = items
        titleTexts = headerTexts
        title.text = headerTexts[0]

        this.items = itemsTexts.mapIndexed { i, s ->
            TextView(context).apply {
                text = s
                visibility = if (expanded) VISIBLE else GONE
                includeFontPadding = false
                setOnTouchListener { _, event ->
                    onItemTouch(i, event)
                    true
                }
                addView(this)
            }
        }

        applyStyle()
        selectItem(0)
    }

    fun setOnItemClickListener(clickListener: ((index: Int, itemText: String) -> Unit)?) {
        userOnItemClickListener = clickListener
    }

//    TODO: fun setOnEndIconClickListener(clickListener: (() -> Unit)?) {
//        endIconClickListener = clickListener
//    }

    fun setShadowView(@IdRes viewId: Int) {
        shadowViewId = viewId
        initShadowView()
    }

    private fun initStyle(attrs: AttributeSet, defStyleAttr: Int) {
        context.withStyledAttributes(attrs, R.styleable.HeaderFilter, defStyleAttr) {
            titleTextSize = getDimension(R.styleable.HeaderFilter_titleTextSize, titleTextSize)
            titleTextColor = getColor(R.styleable.HeaderFilter_titleTextColor, titleTextColor)
            getString(R.styleable.HeaderFilter_titleFontPath)?.let {
                titleTypeFace = Typeface.createFromAsset(context.assets, it)
            }


            buttonSrc = getResourceId(R.styleable.HeaderFilter_buttonSrc, buttonSrc)
            buttonMarginStart = getDimension(
                R.styleable.HeaderFilter_buttonMarginStart,
                buttonMarginStart.toFloat()
            ).roundToInt()
            buttonColor = getColor(R.styleable.HeaderFilter_buttonColor, buttonColor)
            buttonOnClickColor =
                getColor(R.styleable.HeaderFilter_buttonOnClickColor, buttonOnClickColor)

            itemPaddingHorizontal = getDimension(
                R.styleable.HeaderFilter_itemPaddingHorizontal,
                itemPaddingHorizontal.toFloat()
            ).roundToInt()
            itemPaddingVertical = getDimension(
                R.styleable.HeaderFilter_itemPaddingVertical,
                itemPaddingVertical.toFloat()
            ).roundToInt()
            itemTextSize = getDimension(R.styleable.HeaderFilter_itemTextSize, itemTextSize)
            itemTextColor = getColor(R.styleable.HeaderFilter_itemTextColor, itemTextColor)
            itemOnClickTextColor =
                getColor(R.styleable.HeaderFilter_itemOnClickTextColor, itemOnClickTextColor)
            itemBackgroundColor =
                getColor(R.styleable.HeaderFilter_itemBackgroundColor, itemBackgroundColor)
            itemOnClickBackgroundColor =
                getColor(
                    R.styleable.HeaderFilter_itemOnClickBackgroundColor,
                    itemOnClickBackgroundColor
                )

            shadowViewId = getResourceId(R.styleable.HeaderFilter_shadowView, shadowViewId)

            menuMarginTop = getDimension(
                R.styleable.HeaderFilter_menuMarginTop,
                menuMarginTop.toFloat()
            ).roundToInt()

            //TODO: endIconSrc = getResourceId(R.styleable.Header_endIconDrawable, endIconSrc)
        }
    }

    private fun applyStyle() {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
        title.setTextColor(titleTextColor)
        title.typeface = titleTypeFace

        expandButton.setImageResource(buttonSrc)
        expandButton.setColor(buttonColor)
        expandButton.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            marginStart = buttonMarginStart
        }

        items.forEach {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize)
            it.setTextColor(itemTextColor)
            it.setBackgroundColor(itemBackgroundColor)
            it.setPadding(
                itemPaddingHorizontal,
                itemPaddingVertical,
                itemPaddingHorizontal,
                itemPaddingVertical
            )
            it.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        if (items.isNotEmpty()) {
            (items[0].layoutParams as LayoutParams).apply {
                topMargin = menuMarginTop
            }
        }
    }

    private fun expandOrCollapseMenu() {
        if (expanded) collapseMenu()
        else expandMenu()
    }

    fun expandMenu() {
        if (expanded) return
        expanded = true
        expandButton.setColor(buttonOnClickColor)
        shadowView?.visibility = VISIBLE
        items.forEach { it.visibility = VISIBLE }
    }

    fun collapseMenu() {
        if (!expanded) return
        expanded = false
        expandButton.setColor(buttonColor)
        shadowView?.visibility = GONE
        items.forEach { it.visibility = GONE }
    }

    private fun selectItem(index: Int) {
        items[selectedItemIndex].setBackgroundColor(itemBackgroundColor)
        items[selectedItemIndex].setTextColor(itemTextColor)
        items[index].setBackgroundColor(itemOnClickBackgroundColor)
        items[index].setTextColor(itemOnClickTextColor)
    }

    private fun ImageView.setColor(color: Int) = setColorFilter(color, PorterDuff.Mode.SRC_IN)
}
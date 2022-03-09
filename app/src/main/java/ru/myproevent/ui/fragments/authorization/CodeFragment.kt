package ru.myproevent.ui.fragments.authorization

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentCodeBinding
import ru.myproevent.domain.utils.pxValue
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.authorization.code.CodePresenter
import ru.myproevent.ui.presenters.authorization.code.CodeView
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.views.KeyboardAwareTextInputEditText

class CodeFragment : BaseMvpFragment<FragmentCodeBinding>(FragmentCodeBinding::inflate), CodeView {

    private inner class SingleDigitEditKeyListener(
        private val prevViewInRow: KeyboardAwareTextInputEditText?,
        private val nextViewInRow: KeyboardAwareTextInputEditText?
    ) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER -> {
                        nextViewInRow?.requestFocus() ?: return false
                        return true
                    }
                    KeyEvent.KEYCODE_DEL -> {
                        prevViewInRow?.requestFocus()
                        prevViewInRow?.setSelection(prevViewInRow.length())
                        return false
                    }
                }
            }
            return false
        }
    }

    private inner class SingleDigitEditTextWatcher(
        private val nextViewInRow: KeyboardAwareTextInputEditText?
    ) : TextWatcher {
        private var lastValue = ""
        private var isFirstValue = true
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
            lastValue = s.toString()
        }

        override fun afterTextChanged(s: Editable) {
            if (isFirstValue) {
                isFirstValue = false
                nextViewInRow?.requestFocus()
            }
            var newValue = ""
            if (s.toString().length > 1) {
                newValue = if (s[0].toString() == lastValue) {
                    s.subSequence(1, 2).toString()
                } else {
                    s.subSequence(0, 1).toString()
                }
                s.replace(0, s.length, lastValue)
            }
            if (newValue.isNotEmpty()) {
                nextViewInRow?.requestFocus()
                nextViewInRow?.text = SpannableStringBuilder(newValue)
            }

            presenter.codeEdit()
        }
    }

    private val numberStringBuilder = StringBuilder()

    private fun getVerificationCode() = with(binding) {
        numberStringBuilder.clear()
        numberStringBuilder.append(digit1Edit.text)
        numberStringBuilder.append(digit2Edit.text)
        numberStringBuilder.append(digit3Edit.text)
        numberStringBuilder.append(digit4Edit.text)
        return@with numberStringBuilder.toString()
    }

    // TODO: отрефакторить, эта функция во многом копирует setLayoutParams в AuthorizationFragment
    private fun setLayoutParams() = with(binding) {
        body.post {
            val availableHeight = root.height

            if (ohNowIRemember.lineCount > 1 || authorize.lineCount > 1) {
                bottomOptionsContainer.orientation = LinearLayout.VERTICAL
                bottomOptionsHorizontalSeparator.visibility = View.GONE
            }

            bodySpace.layoutParams = bodySpace.layoutParams.apply { height = availableHeight }
            body.post {
                val difference = body.height - availableHeight
                if (difference > 0) {
                    logo.isVisible = false
                }
                if (difference > pxValue(80f + 48f)) {
                    formTitle.isVisible = false
                }
            }
        }
    }


    override val presenter by moxyPresenter {
        CodePresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = CodeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutParams()
        codeExplanation.text =
            String.format(getString(R.string.code_explanation_text), presenter.getEmail())
        continueRegistration.setOnClickListener {
            presenter.continueRegistration(getVerificationCode())
        }
        refreshCode.setOnClickListener { presenter.refreshCode() }
        authorize.setOnClickListener { presenter.authorize() }
        ohNowIRemember.setOnClickListener { authorize.performClick() }
        bottomOptionsContainer.setOnClickListener { authorize.performClick() }
        back.setOnClickListener { presenter.onBackPressed() }
        // TODO: отрефакторить - вынести это в кастомные вьюхи
        digit1Edit.selectionChangedListener =
            { _, _ -> digit1Edit.setSelection(digit1Edit.length()) }
        digit1Edit.addTextChangedListener(SingleDigitEditTextWatcher(digit2Edit))
        digit2Edit.selectionChangedListener =
            { _, _ -> digit2Edit.setSelection(digit2Edit.length()) }
        digit2Edit.addTextChangedListener(SingleDigitEditTextWatcher(digit3Edit))
        digit3Edit.selectionChangedListener =
            { _, _ -> digit3Edit.setSelection(digit3Edit.length()) }
        digit3Edit.addTextChangedListener(SingleDigitEditTextWatcher(digit4Edit))
        digit4Edit.selectionChangedListener =
            { _, _ -> digit4Edit.setSelection(digit4Edit.length()) }
        digit4Edit.addTextChangedListener(SingleDigitEditTextWatcher(null))
        digit4Edit.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                digit4Edit.hideKeyBoard()
            }
        }
        digit1Edit.setOnKeyListener(SingleDigitEditKeyListener(null, digit2Edit))
        digit2Edit.setOnKeyListener(SingleDigitEditKeyListener(digit1Edit, digit3Edit))
        digit3Edit.setOnKeyListener(SingleDigitEditKeyListener(digit2Edit, digit4Edit))
        digit4Edit.setOnKeyListener(SingleDigitEditKeyListener(digit3Edit, null))
        back.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    with((v as MaterialButton)) {
                        setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_blue_900))
                        val colorState = ColorStateList(
                            arrayOf(
                                intArrayOf(-android.R.attr.state_focused),
                            ),
                            intArrayOf(
                                requireContext().getColor(R.color.ProEvent_bright_orange_300),
                            )
                        )
                        back.strokeColor = colorState
                    }
                MotionEvent.ACTION_UP -> {
                    (v as MaterialButton).setTextColor(ProEventApp.instance.getColor(R.color.ProEvent_blue_800))
                    val colorState = ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_focused),
                        ),
                        intArrayOf(
                            requireContext().getColor(R.color.ProEvent_blue_800),
                        )
                    )
                    back.strokeColor = colorState
                    v.performClick()
                }
            }
            return@setOnTouchListener false
        }
    }

    override fun onResume() {
        super.onResume()
        // Почему-то если нажать "Уже есть аккаунт",
        // а потом с помощью кнопки back вернуться на этот экран,
        // то digit4Edit получает фокус
        binding.digit4Edit.clearFocus()
    }

    override fun showCodeErrorMessage(message: String?) = with(binding) {
        val colorStates = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(-android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(-android.R.attr.state_focused),
        )

        val defaultColors = resources.getColor(R.color.ProEvent_blue_300, null).let {
            intArrayOf(it, it, it, it, it, it, it, it)
        }

        if (message.isNullOrBlank()) {
            errorMessage.isVisible = false
            digit1.setBoxStrokeColorStateList(ColorStateList(colorStates, defaultColors))
            digit2.setBoxStrokeColorStateList(ColorStateList(colorStates, defaultColors))
            digit3.setBoxStrokeColorStateList(ColorStateList(colorStates, defaultColors))
            digit4.setBoxStrokeColorStateList(ColorStateList(colorStates, defaultColors))
            return@with
        }
        errorMessage.text = message
        errorMessage.isVisible = true

        val errorColors = resources.getColor(R.color.PE_error_color_01, null).let {
            intArrayOf(it, it, it, it, it, it, it, it)
        }

        digit1.setBoxStrokeColorStateList(ColorStateList(colorStates, errorColors))
        digit2.setBoxStrokeColorStateList(ColorStateList(colorStates, errorColors))
        digit3.setBoxStrokeColorStateList(ColorStateList(colorStates, errorColors))
        digit4.setBoxStrokeColorStateList(ColorStateList(colorStates, errorColors))
    }
}
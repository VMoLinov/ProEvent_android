package ru.myproevent.ui.fragments.authorization

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.DialogLicenseBinding
import ru.myproevent.databinding.FragmentRegistrationBinding
import ru.myproevent.domain.models.Suggestion
import ru.myproevent.domain.utils.pxValue
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.authorization.registration.RegistrationPresenter
import ru.myproevent.ui.presenters.authorization.registration.RegistrationView
import ru.myproevent.ui.presenters.main.RouterProvider
import java.lang.reflect.Field
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import java.util.concurrent.TimeUnit

class RegistrationFragment :
    BaseMvpFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate),
    RegistrationView {

    var adapter: ArrayAdapter<String>? = null

    // TODO: вынести в кастомную вьюху
    private val licenceTouchListener = View.OnTouchListener { v, event ->
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

    // TODO: отрефакторить, эта функция во многом копирует setLayoutParams в AuthorizationFragment
    private fun setLayoutParams() = with(binding) {
        body.post {
            val availableHeight = root.height

            if (ohNowIRemember.lineCount > 1 || authorize.lineCount > 1) {
                bottomOptionsContainer.orientation = LinearLayout.VERTICAL
                bottomOptionsHorizontalSeparator.visibility = GONE
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
        RegistrationPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = RegistrationFragment()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutParams()

        emailEdit.doAfterTextChanged {
            presenter.emailEdited()
        }
        adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf())
        binding.emailEdit.setAdapter(adapter)

        RxTextView.textChangeEvents(emailEdit)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe {
                presenter.typedEmail(it.text().toString())
            }


        passwordEdit.doAfterTextChanged {
            presenter.passwordEdited()
        }
        passwordConfirmEdit.doAfterTextChanged {
            presenter.passwordConfirmEdited()
        }

        licenseText.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(
                DialogLicenseBinding.inflate(layoutInflater).apply {
                    // TODO: вынести в кастомную вьюху
                    license1.setOnTouchListener(licenceTouchListener)
                    license2.setOnTouchListener(licenceTouchListener)
                }.root
            )
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            // https://stackoverflow.com/questions/46861306/how-to-disable-bottomsheetdialogfragment-dragging
            try {
                val behaviorField: Field =
                    bottomSheetDialog.javaClass.getDeclaredField("behavior")
                behaviorField.isAccessible = true
                val behavior = behaviorField.get(bottomSheetDialog) as BottomSheetBehavior<*>
                behavior.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            bottomSheetDialog.show()
        }
        licenseCheckboxHitArea.setOnClickListener { licenseCheckbox.touch() }
        continueRegistration.setOnClickListener {
            presenter.continueRegistration(
                licenseCheckbox.isChecked,
                emailEdit.text.toString(),
                passwordEdit.text.toString(),
                passwordConfirmEdit.text.toString()
            )
        }
        bottomOptionsContainer.setOnClickListener { presenter.signup() }

        val licenseTextIAgree: Spannable =
            SpannableString(getString(R.string.license_i_agree_with))
        licenseTextIAgree.setSpan(
            ForegroundColorSpan(ProEventApp.instance.getColor(R.color.ProEvent_blue_800)),
            0,
            licenseTextIAgree.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        licenseText.text = licenseTextIAgree

        val licenseTextConditionOne: Spannable =
            SpannableString(getString(R.string.license_condition_1))
        licenseTextConditionOne.setSpan(
            ForegroundColorSpan(ProEventApp.instance.getColor(R.color.ProEvent_bright_orange_500)),
            0,
            licenseTextConditionOne.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        licenseText.append(licenseTextConditionOne)

        val licenseTextConditionSeparator: Spannable =
            SpannableString(getString(R.string.license_condition_separator))
        licenseTextConditionSeparator.setSpan(
            ForegroundColorSpan(ProEventApp.instance.getColor(R.color.ProEvent_blue_800)),
            0,
            licenseTextConditionSeparator.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        licenseText.append(licenseTextConditionSeparator)

        val licenseTextConditionTwo: Spannable =
            SpannableString(getString(R.string.license_condition_2))
        licenseTextConditionTwo.setSpan(
            ForegroundColorSpan(ProEventApp.instance.getColor(R.color.ProEvent_bright_orange_500)),
            0,
            licenseTextConditionTwo.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        licenseText.append(licenseTextConditionTwo)

        // https://stackoverflow.com/a/33816251
        ViewCompat.setTranslationZ(requireView(), 100f)
    }

    private fun showErrorMessage(message: String?, errorView: TextView, space: Space) {
        if (message.isNullOrBlank()) {
            errorView.visibility = GONE
            space.visibility = VISIBLE
            return
        }
        errorView.text = message
        errorView.visibility = VISIBLE
        space.visibility = GONE
    }

    override fun showEmailErrorMessage(message: String?) =
        showErrorMessage(
            message,
            binding.emailErrorMessage,
            binding.passwordInputContainerTopSeparator
        )

    override fun showPasswordErrorMessage(message: String?) =
        showErrorMessage(
            message,
            binding.passwordInputErrorMessage,
            binding.passwordConfirmInputContainerTopSeparator
        )

    override fun showPasswordConfirmErrorMessage(message: String?) =
        showErrorMessage(
            message,
            binding.passwordConfirmErrorMessage,
            binding.passwordConfirmInputContainerBottomSeparator
        )

    override fun setEmailHint(emailSuggestion: List<Suggestion>) {
        adapter?.clear()
        adapter?.addAll(emailSuggestion.map { it.value })
        adapter?.filter?.filter(binding.emailEdit.text, null);
    }
}
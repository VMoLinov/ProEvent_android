package ru.myproevent.ui.fragments.authorization

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentAuthorizationBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.authorization.authorization.AuthorizationPresenter
import ru.myproevent.ui.presenters.authorization.authorization.AuthorizationView
import ru.myproevent.ui.presenters.main.BottomNavigation
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.presenters.main.Tab
import android.widget.LinearLayout
import androidx.core.view.isVisible
import ru.myproevent.domain.models.Suggestion
import ru.myproevent.domain.utils.pxValue

class AuthorizationFragment :
    BaseMvpFragment<FragmentAuthorizationBinding>(FragmentAuthorizationBinding::inflate),
    AuthorizationView {

    private var emailInvalidError = false
    private var passwordInvalidError = false

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun setLayoutParams() = with(binding) {
        // https://stackoverflow.com/a/24035591/11883985
        body.post {
            val availableHeight = root.height

            if(passwordRecovery.lineCount > 1|| registration.lineCount > 1){
                bottomOptionsContainer.orientation = LinearLayout.VERTICAL
                bottomOptionsHorizontalSeparator.visibility = GONE
            }

            space.layoutParams = space.layoutParams.apply { height = availableHeight }
            body.post {
                val difference = body.height - availableHeight
                authorizationConfirmMarginTop.layoutParams =
                    authorizationConfirmMarginTop.layoutParams.apply {
                        height -= if (difference <= pxValue(40f)) {
                            difference
                        } else {
                            pxValue(40f)
                        }.toInt()
                    }
                if(difference > pxValue(40f)){
                    logo.isVisible = false
                }
                if(difference > pxValue(80f + 48f)){
                    formTitle.isVisible = false
                }
            }
        }
    }

    override val presenter by moxyPresenter {
        AuthorizationPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = AuthorizationFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutParams()
        with(binding) {
            authorizationConfirm.setOnClickListener {
                presenter.authorize(
                    emailEdit.text.toString(),
                    passwordEdit.text.toString(),
                    rememberMeCheckbox?.isChecked ?: true
                )
            }
            registration.setOnClickListener {
                presenter.openRegistration()
            }
            registrationHitArea.setOnClickListener {
                registration.performClick()
            }
            passwordRecovery.setOnClickListener { presenter.recoverPassword() }
            passwordRecoveryHitArea.setOnClickListener { passwordRecovery.performClick() }

            val colorState = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_active),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(-android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(
                    requireContext().getColor(R.color.ProEvent_blue_300),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_blue_300),
                    requireContext().getColor(R.color.ProEvent_blue_300),
                    requireContext().getColor(R.color.ProEvent_blue_300)
                )
            )
            emailEdit.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    emailInvalidError = false
                    emailInput.setBoxStrokeColorStateList(colorState)

                    if (!passwordInvalidError) {
                        errorMessage.visibility = GONE
                    }
                }
            }
            binding.emailEdit.initHints { presenter.typedEmail(it) }
            passwordEdit.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    passwordInvalidError = false
                    passwordInput.setBoxStrokeColorStateList(colorState)

                    if (!emailInvalidError) {
                        errorMessage.visibility = GONE
                    }

                    passwordInput.setEndIconTintList(
                        ColorStateList(
                            arrayOf(intArrayOf()),
                            intArrayOf(requireContext().getColor(R.color.ProEvent_blue_300))
                        )
                    )
                } else {
                    passwordInput.setEndIconTintList(
                        ColorStateList(
                            arrayOf(intArrayOf()),
                            intArrayOf(requireContext().getColor(R.color.ProEvent_blue_800))
                        )
                    )
                }
            }
        }
    }

    override fun authorizationDataInvalid() {
        with(binding) {
            val colorState = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_active),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(-android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(
                    requireContext().getColor(R.color.ProEvent_bright_orange_300),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_blue_600),
                    requireContext().getColor(R.color.ProEvent_bright_orange_300),
                    requireContext().getColor(R.color.ProEvent_bright_orange_300),
                    requireContext().getColor(R.color.ProEvent_bright_orange_300)
                )
            )
            emailInvalidError = true
            passwordInvalidError = true
            emailInput.setBoxStrokeColorStateList(colorState)
            passwordInput.setBoxStrokeColorStateList(colorState)
            errorMessage.text = getString(R.string.authorization_data_invalid)
            errorMessage.visibility = VISIBLE
            false
        }
    }

    override fun finishAuthorization() {
        (requireActivity() as BottomNavigation).openTab(Tab.HOME)
    }

    override fun setEmailHint(emailSuggestion: List<Suggestion>) {
        binding.emailEdit.setEmailHint(emailSuggestion)
    }
}
package ru.myproevent.ui.fragments.authorization

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentRecoveryBinding
import ru.myproevent.domain.utils.pxValue
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.authorization.recovery.RecoveryPresenter
import ru.myproevent.ui.presenters.authorization.recovery.RecoveryView
import ru.myproevent.ui.presenters.main.RouterProvider

class RecoveryFragment : BaseMvpFragment<FragmentRecoveryBinding>(FragmentRecoveryBinding::inflate),
    RecoveryView {

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
        RecoveryPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    companion object {
        fun newInstance() = RecoveryFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutParams()
        with(binding) {
            continueRecovery.setOnClickListener { presenter.resetPassword(emailEdit.text.toString()) }
            bottomOptionsContainer.setOnClickListener { presenter.authorize() }
        }
    }
}
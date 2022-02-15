package ru.myproevent.ui.fragments.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentChatBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.chat.ChatPresenter
import ru.myproevent.ui.presenters.chat.ChatView
import ru.myproevent.ui.presenters.main.RouterProvider

class ChatFragment : BaseMvpFragment<FragmentChatBinding>(FragmentChatBinding::inflate), ChatView {

    // TODO: копирует поле licenceTouchListener из RegistrationFragment
    private val attachOptionTouchListener = View.OnTouchListener { v, event ->
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

    private var isAttachOptionsExpanded = false

    private fun setAttachOptionsVisibility(isVisible: Boolean) = with(binding) {
        if (isVisible) {
            attach.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_bright_orange_300
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            attach.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ProEvent_blue_600
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
        isAttachOptionsExpanded = isVisible
        shadow.isVisible = isVisible
        attachOptions.isVisible = isVisible
    }

    override val presenter by moxyPresenter {
        ChatPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    val router by lazy { (parentFragment as RouterProvider).router }

    companion object {
        fun newInstance() = ChatFragment()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        attach.setOnClickListener { setAttachOptionsVisibility(!isAttachOptionsExpanded) }
        attachHitArea.setOnClickListener { attach.performClick() }
        attachSurvey.setOnTouchListener(attachOptionTouchListener)
        attachFile.setOnTouchListener(attachOptionTouchListener)
        attachLocation.setOnTouchListener(attachOptionTouchListener)
        chatBar.setOnClickListener { router.exit() }
    }
}
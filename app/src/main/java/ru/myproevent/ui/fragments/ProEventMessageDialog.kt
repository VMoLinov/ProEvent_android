package ru.myproevent.ui.fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.myproevent.databinding.DialogProEventMessageBinding

class ProEventMessageDialog :
    DialogFragment() {
    private var _view: DialogProEventMessageBinding? = null
    private val view get() = _view!!

    private val messageText: String by lazy { requireArguments().getString(MESSAGE_ARG)!! }

    companion object {
        private const val MESSAGE_ARG = "MESSAGE"
        fun newInstance(message: String): ProEventMessageDialog {
            val proEventDatePickerDialog = ProEventMessageDialog()
            val args = Bundle()
            args.putString(MESSAGE_ARG, message)
            proEventDatePickerDialog.arguments = args
            return proEventDatePickerDialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _view = DialogProEventMessageBinding.inflate(inflater, container, false)
        return view.apply {
            with(view) {
                close.setOnClickListener { dismiss() }
                messageView.text = SpannableStringBuilder(messageText)
                ok.setOnClickListener { dismiss() }
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
package ru.myproevent.ui.fragments.settings

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.KeyListener
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentSecurityBinding
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.presenters.settings.security.SecurityPresenter
import ru.myproevent.ui.presenters.settings.security.SecurityView
import ru.myproevent.ui.views.KeyboardAwareTextInputEditText

// TODO: рефакторинг: Данный фрагмент во многом копирует AccountFragment. Вынести общее в абстрактынй класс
class SecurityFragment : BaseMvpFragment<FragmentSecurityBinding>(FragmentSecurityBinding::inflate),
    SecurityView {

    override val presenter by moxyPresenter {
        SecurityPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    private fun showKeyBoard(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setEditListeners(
        textInput: TextInputLayout,
        textEdit: KeyboardAwareTextInputEditText
    ) {
        textEdit.keyListener = null
        textInput.setEndIconOnClickListener {
            textEdit.keyListener = defaultKeyListener
            textEdit.requestFocus()
            showKeyBoard(textEdit)
            textEdit.text?.let { it1 -> textEdit.setSelection(it1.length) }
            textInput.endIconMode = TextInputLayout.END_ICON_NONE
            showSaveButton()
        }
    }

    private fun showSaveButton() {
        if (binding.save.visibility == GONE) {
            binding.save.visibility = VISIBLE
        }
    }

    private lateinit var defaultKeyListener: KeyListener

    companion object {
        fun newInstance() = SecurityFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        defaultKeyListener = emailEdit.keyListener
        setEditListeners(emailInput, emailEdit)
        setEditListeners(loginInput, loginEdit)
        newPasswordEdit.doAfterTextChanged { showSaveButton() }
        save.setOnClickListener {
            if (oldPasswordEdit.text.toString() != newPasswordEdit.text.toString() || oldPasswordEdit.text.toString() != confirmPasswordEdit.text.toString()) {
                Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            presenter.saveProfile(
                email = emailEdit.text.toString(),
                login = loginEdit.text.toString()
            )
        }
        titleButton.setOnClickListener { presenter.onBackPressed() }

        presenter.getProfile()
    }

    override fun showProfile(profile: Profile) {
        with(binding) {
            with(profile) {
                email?.let { emailEdit.text = SpannableStringBuilder(it) }
                nickName?.let { loginEdit.text = SpannableStringBuilder(it) }
            }
        }
    }

    override fun makeProfileEditable() {
        // TODO:
        showMessage("makeProfileEditable()")
    }
}
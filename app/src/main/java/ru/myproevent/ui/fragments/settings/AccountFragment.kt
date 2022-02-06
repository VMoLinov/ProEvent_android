package ru.myproevent.ui.fragments.settings

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.KeyListener
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentAccountBinding
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.utils.PhoneTextWatcher
import ru.myproevent.domain.models.ProfileDto
import ru.myproevent.domain.utils.GlideLoader
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.presenters.settings.account.AccountPresenter
import ru.myproevent.ui.presenters.settings.account.AccountView
import ru.myproevent.ui.views.CropImageHandler
import ru.myproevent.ui.views.KeyboardAwareTextInputEditText
import ru.myproevent.ui.views.cropimage.CropImageHandler
import ru.myproevent.ui.views.cropimage.CropImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AccountFragment : BaseMvpFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate),
    AccountView {

    private val calendar: Calendar = Calendar.getInstance()
    private var newPictureUUID: String? = null
    var currYear: Int = calendar.get(Calendar.YEAR)
    var currMonth: Int = calendar.get(Calendar.MONTH)
    var currDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    private lateinit var defaultKeyListener: KeyListener
    private lateinit var phoneKeyListener: KeyListener
    private val imageLoader = GlideLoader().apply { ProEventApp.instance.appComponent.inject(this) }
    private val dateEditClickListener = View.OnClickListener {
        // TODO: отрефакторить
        // https://github.com/terrakok/Cicerone/issues/106
        val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
        val prev: Fragment? = parentFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        var pickerYear = currYear
        var pickerMonth = currMonth
        var pickerDay = currDay
        if (!binding.dateOfBirthEdit.text.isNullOrEmpty()) {
            val pickerDate = GregorianCalendar().apply {
                time =
                    SimpleDateFormat(getString(R.string.dateFormat)).parse(binding.dateOfBirthEdit.text.toString())
            }
            pickerYear = pickerDate.get(Calendar.YEAR)
            pickerMonth = pickerDate.get(Calendar.MONTH)
            pickerDay = pickerDate.get(Calendar.DATE)
        }
        val newFragment: DialogFragment =
            ProEventDatePickerDialog.newInstance(pickerYear, pickerMonth, pickerDay).apply {
                onDateSetListener = { year, month, dayOfMonth ->
                    val gregorianCalendar = GregorianCalendar(
                        year, month, dayOfMonth
                    )
                    this@AccountFragment.binding.dateOfBirthEdit.text = SpannableStringBuilder(
                        // TODO: для вывода сделать local date format
                        SimpleDateFormat(getString(R.string.dateFormat)).apply {
                            calendar = gregorianCalendar
                        }.format(
                            gregorianCalendar.time
                        )
                    )
                }
            }
        newFragment.show(ft, "dialog")
    }

    private fun showKeyBoard(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override val presenter by moxyPresenter {
        AccountPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
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
            textInput.endIconMode = END_ICON_NONE
            binding.save.visibility = VISIBLE
        }
    }

    private fun setPhoneListeners(
        textInput: TextInputLayout,
        textEdit: KeyboardAwareTextInputEditText
    ) {
        textEdit.isFocusableInTouchMode = false
        textEdit.keyListener = null
        textInput.setEndIconOnClickListener {
            textEdit.isFocusableInTouchMode = true
            textEdit.keyListener = phoneKeyListener
            textEdit.requestFocus()
            showKeyBoard(textEdit)
            textEdit.text?.let { it1 -> textEdit.setSelection(it1.length) }
            textInput.endIconMode = END_ICON_NONE
            binding.save.visibility = VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        // TODO Передавать фрагмент слишком жирно, нужно придумать что проще
        initCrop()
        defaultKeyListener = nameEdit.keyListener
        setEditListeners(nameInput, nameEdit)
        phoneKeyListener = phoneEdit.keyListener
        setPhoneListeners(phoneInput, phoneEdit)
        dateOfBirthEdit.keyListener = null
        dateOfBirthEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                dateOfBirthEdit.performClick()
            }
        }
        dateOfBirthInput.setEndIconOnClickListener {
            dateOfBirthEdit.requestFocus()
            dateOfBirthEdit.setOnClickListener(dateEditClickListener)
            dateOfBirthEdit.performClick()
            dateOfBirthInput.endIconMode = END_ICON_NONE
            save.visibility = VISIBLE
        }
        setEditListeners(positionInput, positionEdit)
        setEditListeners(roleInput, roleEdit)
        save.setOnClickListener {
            presenter.saveProfile(
                nameEdit.text.toString(),
                "+7 ${phoneEdit.text.toString()}",
                dateOfBirthEdit.text.toString(),
                positionEdit.text.toString(),
                roleEdit.text.toString(),
                newPictureUri
            )
        }
        save.setOnClickListener { saveProfile(newPictureUUID) }
        titleButton.setOnClickListener { presenter.onBackPressed() }
        phoneEdit.addTextChangedListener(PhoneTextWatcher())
        presenter.getProfile()
    }

    private fun initCrop() {
        CropImageHandler(
            viewOnClick = binding.editUserImage,
            pickImageCallback = { pickImageActivityContract, cropActivityResultLauncher ->
                registerForActivityResult(pickImageActivityContract) {
                    it?.let { uri -> cropActivityResultLauncher.launch(uri) }
                }
            },
            cropCallback = { cropActivityContract ->
                registerForActivityResult(cropActivityContract) {
                    it?.let { uri ->
                        imageLoader.loadCircle(binding.userImageView, uri)
                        newPictureUri(uri)
                    }
                }
            },
            isCircle = true
        ).init()
    }

    private fun saveProfile(uuid: String?) = with(binding) {
        presenter.saveProfile(
            nameEdit.text.toString(),
            phoneEdit.text.toString(),
            dateOfBirthEdit.text.toString(),
            positionEdit.text.toString(),
            roleEdit.text.toString(),
            uuid.orEmpty()
        )
    }

    private fun saveCallBack(uuid: String?) {
        newPictureUUID?.let { presenter.deleteImage(it) }
        newPictureUUID = uuid
        saveProfile(newPictureUUID)
    }

    private fun newPictureUri(uri: Uri) {
        presenter.saveImage(File(uri.path.orEmpty()), ::saveCallBack)
    }

    override fun showProfile(profile: Profile) {
        with(binding) {
            with(profile) {
                fullName?.let { nameEdit.text = SpannableStringBuilder(it) }
                phone?.let { phoneEdit.setText(it.subSequence(3, it.length)) }
                birthdate?.let { dateOfBirthEdit.text = SpannableStringBuilder(it) }
                position?.let { positionEdit.text = SpannableStringBuilder(it) }
                description?.let { roleEdit.text = SpannableStringBuilder(it) }
                imgUri?.let {
                    newPictureUUID = it
                    imageLoader.loadCircle(binding.userImageView, it)
                }
            }
        }
    }

    override fun makeProfileEditable() {
        // TODO:
        showMessage("makeProfileEditable()")
    }

    companion object {
        fun newInstance() = AccountFragment()
    }
}

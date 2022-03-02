package ru.myproevent.ui.fragments.settings

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.annotation.RequiresApi
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentAccountBinding
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.utils.GlideLoader
import ru.myproevent.domain.utils.PhoneTextWatcher
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.main.RouterProvider
import ru.myproevent.ui.presenters.settings.account.AccountPresenter
import ru.myproevent.ui.presenters.settings.account.AccountView
import ru.myproevent.ui.views.CropImageHandler
import ru.myproevent.ui.views.TextInputLayoutEditTool
import java.io.File

class AccountFragment : BaseMvpFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate),
    AccountView {

    private var newPictureUUID: String? = null
    private val imageLoader = GlideLoader().apply { ProEventApp.instance.appComponent.inject(this) }

    override val presenter by moxyPresenter {
        AccountPresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        // TODO Передавать фрагмент слишком жирно, нужно придумать что проще
        initCrop()
        dateOfBirthEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                dateOfBirthEdit.performClick()
            }
        }
        initEditListeners()
        save.setOnClickListener { saveProfile(newPictureUUID) }
        titleButton.setOnClickListener { presenter.onBackPressed() }
        phoneEdit.addTextChangedListener(PhoneTextWatcher())
        cancel.setOnClickListener { presenter.cancelEdit() }
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
            if (phoneEdit.text.toString().isNotBlank()) "+7 ${phoneEdit.text.toString()}" else "",
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
                if (!phone.isNullOrBlank()) {
                    phoneEdit.setText(phone!!.subSequence(3, phone!!.length))
                }
                birthdate?.let { dateOfBirthEdit.text = SpannableStringBuilder(it) }
                position?.let { positionEdit.text = SpannableStringBuilder(it) }
                description?.let { roleEdit.text = SpannableStringBuilder(it) }
                imgUri?.let {
                    newPictureUUID = it
                    imageLoader.loadCircle(binding.userImageView, it)
                }
                groupEdit.visibility = GONE
            }
        }
    }

    private fun initEditListeners() {
        with(binding) {
            nameInput.setEditListeners(nameEdit, presenter::clickOnEditIcon)
            phoneInput.setEditListeners(phoneEdit, presenter::clickOnEditIcon)
            positionInput.setEditListeners(positionEdit, presenter::clickOnEditIcon)
            roleInput.setEditListeners(roleEdit, presenter::clickOnEditIcon)
            dateOfBirthInput.setDialogDate(parentFragmentManager)
            dateOfBirthInput.setEditListeners(dateOfBirthEdit, presenter::clickOnEditIcon)
        }
    }

    override fun makeProfileEditable() {
        // TODO:
        showMessage("makeProfileEditable()")
    }

    override fun setFieldEdited(ids: Map<Int, Boolean>) {
        ids.keys.forEach {
            requireActivity().findViewById<TextInputLayoutEditTool>(it)
                .setRedacting(ids[it] ?: false)
        }
        binding.groupEdit.visibility = if (ids.values.contains(true)) VISIBLE else GONE
    }

    companion object {
        fun newInstance() = AccountFragment()
    }

    override fun showMessage(message: String) {
        Toast.makeText(ProEventApp.instance, message, Toast.LENGTH_LONG).show()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { binding.dateOfBirthInput.firstTime = false }
    }
}

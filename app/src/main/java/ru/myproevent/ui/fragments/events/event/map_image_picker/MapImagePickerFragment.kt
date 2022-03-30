package ru.myproevent.ui.fragments.events.event.map_image_picker

import android.net.Uri
import android.os.Bundle
import android.view.View
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentMapImagePickerBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.map_image_picker.MapImagePickerPresenter
import ru.myproevent.ui.presenters.events.event.map_image_picker.MapImagePickerView
import ru.myproevent.ui.presenters.main.RouterProvider
import java.io.File
import java.io.FileOutputStream


class MapImagePickerFragment :
    BaseMvpFragment<FragmentMapImagePickerBinding>(FragmentMapImagePickerBinding::inflate),
    MapImagePickerView {

    private var file: File? = null

    companion object {
        const val MAP_IMAGE_URI_ARG = "MAP_IMAGE_URI"
        fun newInstance(uri: Uri) = MapImagePickerFragment().apply {
            arguments = Bundle().apply { putParcelable(MAP_IMAGE_URI_ARG, uri) }
        }
    }

    override val presenter by moxyPresenter {
        MapImagePickerPresenter(
            requireArguments().getParcelable(
                MAP_IMAGE_URI_ARG
            )!!,
            (parentFragment as RouterProvider).router,
        ).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            saveEdits.setOnClickListener {
                file?.let { presenter.saveMapImage(it) }
            }
        }
    }

    override fun initImageCropper(uri: Uri) {
        binding.mapImage.setImageURI(uri)
        file = File(requireActivity().cacheDir, "cacheFileAppeal")
        val outputStream = context?.contentResolver?.openInputStream(uri)
        FileOutputStream(file).use { output ->
            val buffer = ByteArray(4 * 1024) // or other buffer size
            var read = 0
            while (outputStream?.read(buffer)?.also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
            outputStream.close()
        }
    }
}

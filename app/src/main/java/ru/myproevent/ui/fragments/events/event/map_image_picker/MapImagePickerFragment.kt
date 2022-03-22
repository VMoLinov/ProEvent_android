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

class MapImagePickerFragment :
    BaseMvpFragment<FragmentMapImagePickerBinding>(FragmentMapImagePickerBinding::inflate),
    MapImagePickerView {
    companion object {
        val MAP_IMAGE_URI_ARG = "MAP_IMAGE_URI"
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
        with(binding){
            saveEdits.setOnClickListener {
                presenter.saveMapImage()
            }
        }
    }

    override fun initImageCropper(uri: Uri) {
        binding.mapImage.setImageURI(uri)
    }
}


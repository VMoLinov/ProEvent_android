package ru.myproevent.ui.fragments.events.event.map_image_picker

import android.os.Bundle
import android.view.View
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.databinding.FragmentMapCreatorBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.map_image_picker.map_creator.MapCreatorPresenterPresenter
import ru.myproevent.ui.presenters.events.event.map_image_picker.map_creator.MapCreatorView
import ru.myproevent.ui.presenters.main.RouterProvider

class MapCreatorFragment :
    BaseMvpFragment<FragmentMapCreatorBinding>(FragmentMapCreatorBinding::inflate),
    MapCreatorView {
    companion object {
        const val MAP_IMAGE_UUID_ARG = "MAP_IMAGE_UUID"
        fun newInstance(mapImageUUID: String) = MapImagePickerFragment().apply {
            arguments = Bundle().apply { putString(MAP_IMAGE_UUID_ARG, mapImageUUID) }
        }
    }

    override val presenter by moxyPresenter {
        MapCreatorPresenterPresenter(
            requireArguments().getParcelable(
                MAP_IMAGE_UUID_ARG
            )!!,
            (parentFragment as RouterProvider).router,
        ).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            addMap.setOnClickListener{
                presenter.addMap(mapNameEdit.text.toString())
            }
        }
    }

    override fun setResult(requestKey: String, result: Bundle) {
        parentFragmentManager.setFragmentResult(requestKey, result)
    }
}

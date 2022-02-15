package ru.myproevent.ui.fragments.events.event

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import moxy.ktx.moxyPresenter
import ru.myproevent.ProEventApp
import ru.myproevent.R
import ru.myproevent.databinding.FragmentAddEventPlaceBinding
import ru.myproevent.ui.fragments.BaseMvpFragment
import ru.myproevent.ui.presenters.events.event.addEventPlace.AddEventPlacePresenter
import ru.myproevent.ui.presenters.main.RouterProvider
import java.io.IOException
import ru.myproevent.domain.models.entities.Address as ProEventAddress

class AddEventPlaceFragment :
    BaseMvpFragment<FragmentAddEventPlaceBinding>(FragmentAddEventPlaceBinding::inflate) {

    private lateinit var map: GoogleMap

    private val locationPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private var initialPlace = LatLng(55.7539333, 37.6186063)

    private lateinit var currentPlace: ProEventAddress

    private val checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        initMyLocationButton()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPlace, 15f))
        setMarker(initialPlace)
        getAddressAsync(initialPlace)
        map.setOnMapClickListener { latLng ->
            getAddressAsync(latLng)
            setMarker(latLng)
            if (binding.buttonConfirm.visibility == View.GONE)
                binding.buttonConfirm.visibility = View.VISIBLE
        }
    }

    override val presenter by moxyPresenter {
        AddEventPlacePresenter((parentFragment as RouterProvider).router).apply {
            ProEventApp.instance.appComponent.inject(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()
        setInitialPlace()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(onMapReadyCallback)
        initSearchButton()
        initConfirmButton()
        initTitle()
    }

    private fun initSearchButton() {
        binding.searchButton.setOnClickListener {
            val geoCoder = Geocoder(it.context)
            val searchText = binding.searchEditText.text.toString()
            Thread {
                try {
                    val addresses = geoCoder.getFromLocationName(searchText, 1)
                    if (addresses.size > 0) {
                        goToAddress(addresses[0])
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun initConfirmButton() = with(binding) {
        buttonConfirm.setOnClickListener {
            parentFragmentManager.setFragmentResult(ADD_EVENT_PLACE_REQUEST_KEY, Bundle().apply {
                putParcelable(
                    ADD_EVENT_PLACE_RESULT, currentPlace
                )
            })
            presenter.backTo()
        }
    }

    private fun initTitle() = with(binding) {
        titleButton.setOnClickListener { presenter.backTo() }
    }

    private fun getMyLocation(callback: (LatLng?) -> Unit) {
        if (!locationPermissionGranted) {
            callback(null)
            return
        }
        LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                task.result.apply {
                    callback(LatLng(latitude, longitude))
                    return@addOnCompleteListener
                }
            } else {
                callback(null)
            }
        }
    }

    private fun setInitialPlace() {
        arguments?.getParcelable<ProEventAddress>(BUNDLE_ADDRESS)?.let {
            initialPlace = LatLng(it.latitude, it.longitude)
            currentPlace = it
            binding.textAddress.text = it.addressLine
            return
        }

        if (locationPermissionGranted) {
            getMyLocation { it?.let { initialPlace = it } }
            return
        }

        currentPlace =
            ProEventAddress(
                initialPlace.latitude,
                initialPlace.longitude,
                getString(R.string.default_map_location)
            )
        getAddressAsync(initialPlace)
        binding.buttonConfirm.visibility = View.VISIBLE
    }

    private fun goToAddress(address: Address) {
        val location = LatLng(address.latitude, address.longitude)
        binding.map.post {
            setMarker(location)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun getAddressAsync(location: LatLng) {
        context?.let {
            val geoCoder = Geocoder(it)
            Thread {
                try {
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses.size == 0) {
                        currentPlace = ProEventAddress(location.latitude, location.longitude, "")
                        return@Thread
                    }
                    binding.textAddress.post {
                        binding.textAddress.text = addresses[0].getAddressLine(0)
                    }
                    currentPlace = ProEventAddress(
                        location.latitude,
                        location.longitude,
                        addresses[0].getAddressLine(0)
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun setMarker(location: LatLng): Marker? {
        map.clear()
        return map.addMarker(MarkerOptions().position(location))
    }

    private fun initMyLocationButton() {
        context?.let {
            map.isMyLocationEnabled = locationPermissionGranted
            map.uiSettings.isMyLocationButtonEnabled = locationPermissionGranted
        }
    }

    private fun checkLocationPermission() {
        when {
            locationPermissionGranted -> Unit
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showRationaleDialog()
            }
            else -> requestPermission()
        }
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.geolocation_access_title))
                .setMessage(getString(R.string.geolocation_access_message))
                .setPositiveButton(getString(R.string.geolocation_access_button_text)) { dialog, _ ->
                    binding.root.post {
                        dialog.dismiss()
                        requestPermission()
                    }
                }
                .setNegativeButton(getString(R.string.no_text)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun requestPermission() =
        checkPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)


    companion object {
        private const val BUNDLE_ADDRESS = "ADDRESS"
        fun newInstance(address: ProEventAddress? = null) = AddEventPlaceFragment().apply {
            address?.let { arguments = Bundle().apply { putParcelable(BUNDLE_ADDRESS, address) } }
        }

        const val ADD_EVENT_PLACE_REQUEST_KEY = "ADD_EVENT_PLACE_REQUEST_KEY"
        const val ADD_EVENT_PLACE_RESULT = "ADD_EVENT_PLACE_RESULT"
    }
}

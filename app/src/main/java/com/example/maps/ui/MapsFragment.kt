package com.example.maps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.maps.database.RouteEntity

import com.example.maps.databinding.FragmentMapsBinding
import com.example.maps.service.LocationForegroundService
import com.example.maps.vm.MapsViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point

import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera

import com.mapbox.maps.plugin.locationcomponent.location


class MapsFragment : Fragment(com.example.maps.R.layout.fragment_maps),
    LocationListener {


    private var mapView: MapView? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    lateinit var viewModel: MapsViewModel
    lateinit var mMapboxMap: MapboxMap
    lateinit var permissionsManager: PermissionsManager
    lateinit var locationForegroundService: LocationForegroundService
    private lateinit var userLocation: RouteEntity
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapsBinding.bind(view)
        requestLocationPermission()
        viewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        binding.mapView.location.updateSettings {
            enabled = true
            pulsingEnabled = true

        }

        binding.cardLocation.setOnClickListener {
            getDeviceLocation()
        }

        clickZoom()


    }


    private fun getDeviceLocation() {

        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                val cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(longitude, latitude))
                    .zoom(15.0)
                    .build()
                binding.mapView.getMapboxMap().setCamera(cameraOptions)

            }

        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted
        }
    }

    private fun startLocationUpdates() {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->

//                    val listRoute = ArrayList<Route>()
//                    listRoute.add(Route(1, location.latitude, location.longitude))
////                    viewModel.saveRoute(listRoute)
                    Log.d("TAG", "Location: ${location.latitude}, ${location.longitude}")


                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    @SuppressLint("Lifecycle")
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        startLocationUpdates()

    }

    override fun onResume() {
        super.onResume()
        startLocationForegroundService()
    }

    override fun onPause() {
        super.onPause()
        stopLocationForegroundService()
    }

    private fun startLocationForegroundService() {
        val intent = Intent(activity, LocationForegroundService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)

    }

    @SuppressLint("Lifecycle")
    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    private fun stopLocationForegroundService() {
        val intent = Intent(activity, LocationForegroundService::class.java)
        activity?.stopService(intent)
    }

    @SuppressLint("Lifecycle")
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    @SuppressLint("Lifecycle")
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mapView?.onDestroy()
    }

    private fun clickZoom() {
        binding.cardPlus.setOnClickListener {
            val currentZoom = binding.mapView.getMapboxMap().cameraState.zoom

            val cameraOptions = CameraOptions.Builder()
                .zoom(currentZoom + 1)
                .build()
            val animationOptions = MapAnimationOptions.Builder()
                .duration(2000)
                .build()
            binding.mapView.camera.easeTo(cameraOptions, animationOptions)

        }
        binding.cardMenus.setOnClickListener {
            val currentZoom = binding.mapView.getMapboxMap().cameraState.zoom

            val cameraOptions = CameraOptions.Builder()
                .zoom(currentZoom - 1)
                .build()
            val animationOptions = MapAnimationOptions.Builder()
                .duration(2000)
                .build()
            binding.mapView.camera.easeTo(cameraOptions, animationOptions)
        }
    }

    override fun onLocationChanged(location: Location) {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(location.latitude, location.longitude))
                .zoom(15.0)
                .build()
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to show user location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



/*      mapBox ni birinchi marta ishlatganim uchunmi
        OnMapReadyCallback funksiyasini ishlata olmadim har
        safar kutubxonasini qo`shganimda gradle bilan bog`liq xatolik sodir bo`ldi
 */



//    override fun onMapReady(mapboxMap: com.mapbox.mapboxsdk.maps.MapboxMap) {
//        mapboxMap.addOnMapClickListener { point ->
//            userLocation.startPoint = point.latitude
//            userLocation.endPoint = point.longitude
//            viewModel.addLocation(userLocation)
//
//            true
//        }
//    }


}

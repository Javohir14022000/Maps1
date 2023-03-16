package com.example.maps.vm

import android.R
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

import com.example.maps.database.RouteDao
import com.example.maps.database.RouteDatabase
import com.example.maps.database.RouteEntity
import com.example.maps.repository.MapsRepository
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mapbox.maps.MapboxMap
import kotlinx.coroutines.launch


class MapsViewModel(application: Application) : AndroidViewModel(application){

    private val repository: MapsRepository = MapsRepository(application)

//    private lateinit var locationLiveData: LiveData<List<RouteEntity>>

     fun addLocation(location: RouteEntity) {
        viewModelScope.launch {
            repository.saveRoute(location)
        }
    }



}
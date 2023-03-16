package com.example.maps.repository

import android.app.Application
import androidx.room.Room
import com.example.maps.database.RouteDatabase
import com.example.maps.database.RouteEntity

class MapsRepository(application: Application) {

    private val db: RouteDatabase = Room.databaseBuilder(
        application.applicationContext,
        RouteDatabase::class.java, "route_database"
    ).build()

     fun saveRoute(route: RouteEntity) = db.routeDao().insert(route)
     fun getRoute() = db.routeDao().getAllRouters()
     fun addRoute(list: List<RouteEntity>) = db.routeDao().addRouters(list)
}
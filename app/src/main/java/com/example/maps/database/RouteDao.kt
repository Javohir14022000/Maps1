package com.example.maps.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RouteDao {

    @Insert
     fun insert(route: RouteEntity)

    @Query("select * from route_entity")
     fun getAllRouters(): LiveData<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun addRouters(list: List<RouteEntity>)
}
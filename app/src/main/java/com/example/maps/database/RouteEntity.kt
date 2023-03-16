package com.example.maps.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_entity")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "start_point")
    var startPoint: Double,
    @ColumnInfo(name = "end_point")
    var endPoint: Double
)

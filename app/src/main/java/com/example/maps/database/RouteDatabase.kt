package com.example.maps.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RouteEntity::class], version = 1, exportSchema = false)
abstract class RouteDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao

    companion object {
        @Volatile
        private var INSTANCE: RouteDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): RouteDatabase {

            val instance = Room.databaseBuilder(
                context,
                RouteDatabase::class.java,
                "route_database"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()

            return instance

        }
    }
}
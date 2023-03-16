package com.example.maps.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.maps.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions

class LocationForegroundService : Service(), LocationListener {

    private lateinit var locationManager: LocationManager

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "LocationForegroundServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "LocationForegroundServiceChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

            val notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Location foreground service is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()

            startForeground(NOTIFICATION_ID, notification)
        }
        return START_STICKY
    }

    override fun onLocationChanged(location: Location) {
        Log.d("TAG", "Location: ${location.latitude}, ${location.longitude}")
    }

    override fun onProviderEnabled(provider: String) {
        // provider yoqilganda chaqiriladi
    }

    override fun onProviderDisabled(provider: String) {
        // provider o'chirilganda chaqiriladi
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // provider holati o'zgartirilganda chaqiriladi
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }
}

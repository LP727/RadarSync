package com.example.radarsync.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionHelper {
    companion object {
        fun checkLocationPermissions(appContext: Context): Boolean {
            val permissionsToRequest = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            // Check if all the permissions are already granted
            val allPermissionsGranted = permissionsToRequest.all {
                ContextCompat.checkSelfPermission(
                    appContext,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }

            return allPermissionsGranted
        }
    }
}

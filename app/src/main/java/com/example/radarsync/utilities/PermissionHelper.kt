package com.example.radarsync.utilities

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionHelper {
    companion object {
        fun checkPermissions(permissionArray: Array<String>, appContext: Context): Boolean {
            // Check if all the permissions are already granted
            val allPermissionsGranted = permissionArray.all {
                ContextCompat.checkSelfPermission(
                    appContext,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }

            return allPermissionsGranted
        }
    }
}

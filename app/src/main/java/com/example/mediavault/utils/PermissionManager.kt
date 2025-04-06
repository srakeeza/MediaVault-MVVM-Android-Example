package com.example.mediavault.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicInteger

class PermissionManager(private val activity: Activity) {

    private val requestCodeCounter = AtomicInteger(0)
    private val permissionCallbacks = mutableMapOf<Int, PermissionCallback>()
    private val permissionDenialCount = mutableMapOf<String, Int>()

    fun requestPermission(
        permission: String,
        onGranted: () -> Unit,
        onDenied: () -> Unit,
        onPermanentlyDenied: () -> Unit
    ) {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(activity, permission) ==
            PackageManager.PERMISSION_GRANTED) {
            onGranted()
            return
        }

        // Generate a unique request code
        val requestCode = requestCodeCounter.incrementAndGet()

        // Store the callback
        permissionCallbacks[requestCode] = PermissionCallback(
            permission = permission,
            onGranted = onGranted,
            onDenied = onDenied,
            onPermanentlyDenied = onPermanentlyDenied
        )

        // Request the permission
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val callback = permissionCallbacks[requestCode] ?: return

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            permissionDenialCount.remove(callback.permission)
            callback.onGranted()
        } else {
            // Permission denied
            val denialCount = permissionDenialCount.getOrDefault(callback.permission, 0) + 1
            permissionDenialCount[callback.permission] = denialCount

            if (denialCount >= 2 && !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, callback.permission)) {
                // User clicked "Don't ask again"
                callback.onPermanentlyDenied()
            } else {
                callback.onDenied()
            }
        }

        // Remove the callback
        permissionCallbacks.remove(requestCode)
    }

    data class PermissionCallback(
        val permission: String,
        val onGranted: () -> Unit,
        val onDenied: () -> Unit,
        val onPermanentlyDenied: () -> Unit
    )
}
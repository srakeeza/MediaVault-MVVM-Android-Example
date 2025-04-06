package com.example.mediavault

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.mediavault.adapter.MediaAdapter
import com.example.mediavault.databinding.ActivityMainBinding
import com.example.mediavault.utils.PermissionManager
import com.example.mediavault.viewmodel.MediaViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MediaViewModel by viewModel()
    private lateinit var permissionManager: PermissionManager
    private lateinit var photosAdapter: MediaAdapter
    private lateinit var videosAdapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionManager = PermissionManager(this)
        setupAdapters()
        setupUI()
        observeViewModel()
    }

    private fun setupAdapters() {
        photosAdapter = MediaAdapter(isVideo = false)
        videosAdapter = MediaAdapter(isVideo = true)
    }

    private fun setupUI() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.recyclerView.adapter = photosAdapter
                        checkPhotoPermissionAndLoad()
                    }

                    1 -> {
                        binding.recyclerView.adapter = videosAdapter
                        checkVideoPermissionAndLoad()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> checkPhotoPermissionAndLoad()
                    1 -> checkVideoPermissionAndLoad()
                }
            }
        })

        // Set initial adapter
        binding.recyclerView.adapter = photosAdapter

        // Initial load
        checkPhotoPermissionAndLoad()
    }

    private fun observeViewModel() {
        viewModel.photos.observe(this) { photos ->
            if (binding.tabLayout.selectedTabPosition == 0) {
                binding.emptyView.isVisible = photos.isEmpty()
                binding.recyclerView.isVisible = photos.isNotEmpty()
                photosAdapter.submitList(photos)
            }
        }

        viewModel.videos.observe(this) { videos ->
            if (binding.tabLayout.selectedTabPosition == 1) {
                binding.emptyView.isVisible = videos.isEmpty()
                binding.recyclerView.isVisible = videos.isNotEmpty()
                videosAdapter.submitList(videos)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    private fun checkPhotoPermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        permissionManager.requestPermission(
            permission = permission,
            onGranted = { viewModel.loadPhotos() },
            onDenied = { showPermissionRationale("photos") },
            onPermanentlyDenied = { showSettingsDialog("photos") }
        )
    }

    private fun checkVideoPermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_VIDEO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        permissionManager.requestPermission(
            permission = permission,
            onGranted = { viewModel.loadVideos() },
            onDenied = { showPermissionRationale("videos") },
            onPermanentlyDenied = { showSettingsDialog("videos") }
        )
    }

    private fun showPermissionRationale(mediaType: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Required")
            .setMessage("We need permission to access your $mediaType")
            .setPositiveButton("Try Again") { _, _ ->
                if (mediaType == "photos") checkPhotoPermissionAndLoad()
                else checkVideoPermissionAndLoad()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSettingsDialog(mediaType: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Required")
            .setMessage("Permission to access $mediaType is required for this app to function. Please grant it in app settings.")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.handlePermissionResult(requestCode, permissions, grantResults)
    }
}
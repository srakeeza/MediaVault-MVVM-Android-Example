package com.example.mediavault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediavault.model.MediaItem
import com.example.mediavault.repository.MediaRepository
import kotlinx.coroutines.launch

class MediaViewModel(
    private val repository: MediaRepository
) : ViewModel() {

    private val _photos = MutableLiveData<List<MediaItem>>()
    val photos: LiveData<List<MediaItem>> = _photos

    private val _videos = MutableLiveData<List<MediaItem>>()
    val videos: LiveData<List<MediaItem>> = _videos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadPhotos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _photos.value = repository.getPhotos()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _videos.value = repository.getVideos()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
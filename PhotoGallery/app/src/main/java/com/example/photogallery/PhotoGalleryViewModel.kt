package com.example.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photogallery.api.NasaFetchr

class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData:LiveData<List<photo>>
    init {
        galleryItemLiveData = NasaFetchr().fetchPhotos()
    }

}
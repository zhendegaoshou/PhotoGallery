package com.example.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.File
private const val TAG = "PhotoGalleryFragment"
class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)
        // 从这个地方可以获取你的存储文件地址
        val file_path = getExternalFilesDir(null)?.absolutePath+ File.separator+"photo"
        Log.d(TAG,file_path)
        
        val isFragmentContainerEmpty = savedInstanceState == null
        if(isFragmentContainerEmpty)
        {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer,PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}

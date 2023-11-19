package com.example.photogallery.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.photo
import com.example.photogallery.photoArray
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileOutputStream
// YOU SHOULD CHANGE API_KEY AND FILE NAME TO YOUR OWN KEY AND PATH
private const val API_KEY = "YOUR API KEY"
private const val TAG = "PhotoGalleryFragment"
private const val file_path_root = "YOUR FILE STORAGE ROOT NAME"

class NasaFetchr {
    private val nasaApi:NasaApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nasaApi = retrofit.create(NasaApi::class.java)
        read_photos(file_path_root+"/photos")

    }


    fun read_photos(file_path: String)
    {
        // 判断文件夹是否存在，不存在创建
        val file = File(file_path)
        if(!file.exists())
        {
            file.mkdirs()
            // Log.d(TAG,"photo already created")
        }
        // else
        // {
        //     Log.d(TAG,"File existed")
        // }

    }
    @WorkerThread
    fun fetchPhoto(url:String): Bitmap? {
        // 获取文件的名称
        val file_last_name_array = url.split("/")
        val file_last_name = file_last_name_array.get(file_last_name_array.size-1)
        val file = File(file_path_root+"/photos"+"/"+file_last_name)
        val file_name = file_path_root+"/photos"+"/"+file_last_name
        // 如果文件已存在，那么直接从文件读取
        if (file.exists())
        {
            val fis = file.inputStream()
            val bitmap = BitmapFactory.decodeStream(fis)
            fis.close()
            // Log.d(TAG,"已从文件中读取${file_name}")
            return bitmap
        }
        // 如果不存在，那么就从网络上下载并且存储到文件中
        else
        {
            val response: Response<ResponseBody> = nasaApi.fetchUrlBytes(url).execute()
            val bitmap = response.body() ?.byteStream()?.use(BitmapFactory::decodeStream)
            // Log.i(TAG,"Decoded bitmap=$bitmap from response=$response")
            
            val fos:FileOutputStream = FileOutputStream(file)
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,fos)
            }
            fos.flush()
            fos.close()
            return bitmap
        }

    }

    fun fetchPhotos():LiveData<List<photo>>{
//        val responseLiveData:MutableLiveData<String> = MutableLiveData()

        val responseLiveData:MutableLiveData<List<photo>> = MutableLiveData()
        val flickRequest: Call<photoArray> = nasaApi.fetchPhotos()

        flickRequest.enqueue(object :Callback<photoArray>
        {
            override fun onResponse(call: Call<photoArray>, response: Response<photoArray>) {
//                responseLiveData.value = response.body()
                val photoArray:photoArray? = response.body()

                var photoResponse:List<photo>? = photoArray?.photos
//                var Mar_Photo_items:List<Mar_Photo_item> = photoResponse?.Mar_Photo_items?: mutableListOf()
//                Mar_Photo_items = Mar_Photo_items.filterNot {
//                    it.img_src.isBlank()
//                }
                if (photoResponse != null) {
                    photoResponse = photoResponse.filterNot {
                        it.img_src.isBlank()
                    }
                }
                // Log.d(TAG,"Response Received")
                responseLiveData.value = photoResponse


            }

            override fun onFailure(call: Call<photoArray>, t: Throwable) {

            }

        })

        return responseLiveData
    }


}

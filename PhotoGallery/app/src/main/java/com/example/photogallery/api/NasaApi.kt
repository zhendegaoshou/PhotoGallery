package com.example.photogallery.api
import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.example.photogallery.photoArray
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

private const val API_KEY = "YOUR API KEY"
interface NasaApi {
    @GET(
        "mars-photos/api/v1/rovers/"+
                "curiosity"+
                "/photos?"+
                "sol=1000"+
                "&api_key=$API_KEY"
    )
    fun fetchPhotos():Call<photoArray>

    @GET
    fun fetchUrlBytes(@Url url:String): Call<ResponseBody>


}

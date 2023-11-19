package com.example.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract.Contacts.Photo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.api.NasaFetchr
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment: Fragment() {
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var downloader:Downloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
//        val flickLiveData:LiveData<List<photo>> = NasaFetchr().fetchPhotos()

//        flickLiveData.observe(
//            this,
//            Observer {photo_list->
////                val gson = Gson()
////                val from_json = gson.fromJson(reponseSrting,photoArray::class.java)
////                val photo_list = from_json.photos
////                val jsonObject = JSONArray(reponseSrting).getJSONObject(0)
////                val id = jsonObject.getInt("id")
////                val photo = JSONObject(reponseSrting_temp)
////                val id = photo.getInt("id")
//                Log.d(TAG,"Response :${photo_list.size}")
//
//            }
//        )
        photoGalleryViewModel =
            ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
//        downloader = Downloader()
        val responseHandler = Handler()
           downloader = Downloader(responseHandler) {photoHolder,bitmap ->
               val drawable = BitmapDrawable(resources,bitmap)
               photoHolder.bindDrawable(drawable)

        }
        lifecycle.addObserver(downloader.fragmentLifecycleObserver)
    }



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewLifecycleOwner.lifecycle.addObserver(downloader.viewLifecycleObserver)
        val view = inflater.inflate(R.layout.fragment_photo_gallery,container,false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context,3)

        return view

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            downloader.fragmentLifecycleObserver
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            downloader.viewLifecycleObserver
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer{photo_list ->
//                Log.d(TAG,"Have items from ViewModel $photo_list")
                photoRecyclerView.adapter = PhotoAdapter(photo_list)
            }
        )
    }

    private class PhotoHolder(private val itemImageView:ImageView)
        :RecyclerView.ViewHolder(itemImageView)
    {
//        val bindTitle:(CharSequence) ->Unit = itemTextView::setText
            val bindDrawable:(Drawable) ->Unit = itemImageView::setImageDrawable
    }

    private inner class PhotoAdapter(private val photo_list:List<photo>):RecyclerView.Adapter<PhotoHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
//            val textView = TextView(parent.context)
//            return PhotoHolder(textView)
            val view = layoutInflater.inflate(
                R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int {
            return photo_list.size
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
           val photo = photo_list[position]
//            holder.bindTitle(photo.img_src)
            val placeholder:Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.navi
            )?:ColorDrawable()
            holder.bindDrawable(placeholder)
            downloader.queue(holder,photo.img_src,photo.id)
        }

    }
    companion object
    {
        fun newInstance() = PhotoGalleryFragment()
    }
}
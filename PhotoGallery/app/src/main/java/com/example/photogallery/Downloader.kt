package com.example.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.photogallery.api.NasaFetchr
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private const val TAG="Downloader"
private const val MESSAGE_DOWNLOAD = 0
class Downloader<in T> (private val responseHandler:Handler,
     private val onDownloaded:(T, Bitmap) ->Unit
) :HandlerThread(TAG){


    val fragmentLifecycleObserver:LifecycleObserver =
        object :LifecycleObserver
        {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun setup()
            {
                Log.i(TAG,"Starting background thread")
                start()
                looper
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun tearDown()
            {
                Log.i(TAG,"Destroying background thread")
                quit()
            }

        }

    val viewLifecycleObserver:LifecycleObserver =
        object:LifecycleObserver
        {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearQueue()
            {
                Log.i(TAG,"Clearing all requests from queue")
                requestHandler.removeMessages(MESSAGE_DOWNLOAD)
                requestMap.clear()
            }
        }

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T,String>()
    private val nasaFetchr = NasaFetchr()

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object:Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == MESSAGE_DOWNLOAD)
                {
                    val target = msg.obj as T
                    Log.i(TAG,"Got a request for URL:${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }
    override fun quit():Boolean
    {
        hasQuit = true
        return super.quit()
    }

    private fun handleRequest(target:T)
    {
        val url = requestMap[target] ?: return
        val bitmap = nasaFetchr.fetchPhoto(url) ?: return

        responseHandler.post(Runnable {
            if(requestMap[target] != url || hasQuit)
            {
                return@Runnable
            }
            requestMap.remove(target)
            onDownloaded(target,bitmap)
        })
    }


    fun queue(target:T,url:String,id: Int)
    {
        Log.i(TAG,"Got a URL:$url")
        requestMap[target] = url
        val id_file = id
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget()
    }


}
package com.example.photogallery

data class rover (

    val id:Int,
    val name:String,
    val landing_date:String,
    val launch_date:String,
    val status:String,
    val max_sol:Int,
    val max_date:String,
    val total_photos:Int,
    val cameras: List<rover_camera>
        )
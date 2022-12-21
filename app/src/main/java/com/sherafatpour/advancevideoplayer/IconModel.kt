package com.sherafatpour.advancevideoplayer

data class IconModel(
    var imageView:Int,
    var iconTitle:String,
    var type:IconType
)
enum class IconType { NIGHT,MUTE ,ROTATE,BACK }

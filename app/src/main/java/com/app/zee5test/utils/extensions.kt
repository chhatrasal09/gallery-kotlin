package com.app.zee5test.utils

import android.content.res.Resources

val Int.dp
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.px
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Float.dp
    get() = this * Resources.getSystem().displayMetrics.density
val Float.px
    get() = this / Resources.getSystem().displayMetrics.density
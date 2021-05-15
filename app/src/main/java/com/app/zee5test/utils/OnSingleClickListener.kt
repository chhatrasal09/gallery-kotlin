package com.app.zee5test.utils

import android.os.SystemClock
import android.view.View
import kotlin.math.abs


class OnSingleClickListener : View.OnClickListener {

    private val lastClickMap: MutableMap<View, Long> = hashMapOf()

    private val onClickListener: View.OnClickListener

    constructor(listener: View.OnClickListener) {
        onClickListener = listener
    }

    constructor(listener: (View) -> Unit) {
        onClickListener = View.OnClickListener { listener.invoke(it) }
    }

    override fun onClick(v: View) {
        val previousClickTimestamp = lastClickMap[v]
        val currentTimestamp = SystemClock.uptimeMillis()

        lastClickMap[v] = currentTimestamp
        if (previousClickTimestamp == null || abs(currentTimestamp - previousClickTimestamp) > DELAY_MILLIS) {
            onClickListener.onClick(v)
        }
    }

    companion object {
        // Tweak this value as you see fit. In my personal testing this
        // seems to be good, but you may want to try on some different
        // devices and make sure you can't produce any crashes.
        private const val DELAY_MILLIS = 300L
    }

}
package com.app.zee5test.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey

@BindingAdapter("imageUrlWithThumbnail", "signature")
fun loadImageWithThumbnail(imageView: ImageView, url: String, signature: Any) {
    Glide.with(imageView.context)
        .load(url)
        .format(DecodeFormat.PREFER_RGB_565)
        .override(200.dp, 200.dp)
        .centerCrop()
        .thumbnail(0.25f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(ObjectKey(signature))
        .into(imageView)
}
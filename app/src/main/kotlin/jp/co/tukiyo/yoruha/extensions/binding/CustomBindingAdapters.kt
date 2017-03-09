package jp.co.tukiyo.yoruha.extensions.binding

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide


@BindingAdapter("bind:imageUrl")
fun ImageView.loadImage(imageUrl: String) {
    Glide.with(context)
            .load(imageUrl)
            .placeholder(android.R.drawable.stat_sys_download)
            .dontAnimate()
            .into(this)
}
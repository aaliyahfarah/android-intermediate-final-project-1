package com.bangkit.storyappdicoding.util

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.data.Story
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()

        Glide.with(imageView.context)
            .load(imageUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_baseline_broken_image_24)
            )
            .into(imageView)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Story>?) {
    val adapter = recyclerView.adapter as StoryAdapters
    adapter.submitList(data)
}

@BindingAdapter("apiStatus")
fun bindStatus(statusImageView: ImageView, status: ApiStatus?) {
    when (status) {
        ApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_baseline_cloud_off_24)
        }

        else -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("apiStatus")
fun bindStatus(progressBar: ProgressBar, status: ApiStatus?) {
    when (status) {
        ApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }

        else -> {
            progressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("dateFormat")
fun TextView.setDateFormat(date: String?) {
    if (date.isNullOrEmpty()) return

    text = date.withDateFormat()
}
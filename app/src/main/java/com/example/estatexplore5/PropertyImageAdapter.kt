package com.example.estatexplore5.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.estatexplore5.R

class PropertyImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<PropertyImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewProperty)

        fun bind(url: String) {
            Glide.with(itemView.context)
                .load(url)
                .placeholder(R.drawable.ic_home)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property_image, parent, false)

        // Sayfa çökmesini önlemek için kesin olarak match_parent ayarı yapıyoruz:
        view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size
}



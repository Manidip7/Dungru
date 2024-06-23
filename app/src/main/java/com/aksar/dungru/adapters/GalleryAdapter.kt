package com.aksar.dungru.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R

class GalleryAdapter(private val context: Context, private val imageList: List<String>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.gallery_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_recycler, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val imagePath = imageList[position]
       // Glide.with(context).load(imagePath).into(holder.imageView)
        holder.imageView.setImageBitmap(filePathToBitmap(imagePath))
    }

    private fun filePathToBitmap(filePath: String): Bitmap? {
        /* return try {
            BitmapFactory.decodeFile(filePath)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }*/

        val options = BitmapFactory.Options().apply {
            inSampleSize = 2 // Adjust the sample size based on your requirements
        }
        return BitmapFactory.decodeFile(filePath, options)


    }

    override fun getItemCount(): Int = imageList.size
}
package com.example.estatexplore5.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.estatexplore5.R

data class Property(
    var id: String? = null,
    val imageUrl: String = "",
    val extraImages: List<String> = emptyList(),
    var title: String = "",
    var price: String = "",
    var details: String = "",
    var user: String = "",
    var contact: String = "",
    var area: String = "",
    var rooms: String = "",
    var location: String = "",
    var categories: List<String> = emptyList()
)

class PropertyAdapter(private val propertyList: List<Property>) :
    RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProperty: ImageView = itemView.findViewById(R.id.image_property)
        val textTitle: TextView = itemView.findViewById(R.id.text_title)
        val textPrice: TextView = itemView.findViewById(R.id.text_price)
        val textDetails: TextView = itemView.findViewById(R.id.text_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recommended, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = propertyList[position]

        Glide.with(holder.itemView.context)
            .load(property.imageUrl)
            .placeholder(R.drawable.ic_home)
            .into(holder.imageProperty)

        holder.textTitle.text = property.title
        holder.textPrice.text = formatPrice(property.price)
        holder.textDetails.text = property.location

        holder.itemView.setOnClickListener {
            val fragment = PropertyDetailFragment()
            val bundle = Bundle().apply {
                putString("title", property.title)
                putString("price", formatPrice(property.price))
                putString("details", property.details)
                putString("imageUrl", property.imageUrl)
                putStringArrayList("extraImages", ArrayList(property.extraImages)) // ✅ EKLENDİ
                putString("area", property.area)
                putString("rooms", property.rooms)
                putString("location", property.location)
                putString("contact", property.contact)
                putString("categories", property.categories.joinToString(", "))
            }
            fragment.arguments = bundle

            val activity = holder.itemView.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = propertyList.size

    private fun formatPrice(price: String): String {
        return if (!price.contains("₺")) {
            price.toIntOrNull()?.let {
                String.format("%,d ₺", it).replace(',', '.')
            } ?: "$price ₺"
        } else {
            price
        }
    }
}







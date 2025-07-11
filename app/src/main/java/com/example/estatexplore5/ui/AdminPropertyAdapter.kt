package com.example.estatexplore5.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.estatexplore5.R
import com.example.estatexplore5.ui.home.Property

class AdminPropertyAdapter(
    private val propertyList: MutableList<Property>,
    private val onDeleteClick: (Property) -> Unit,
    private val onEditClick: (Property) -> Unit
) : RecyclerView.Adapter<AdminPropertyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProperty: ImageView = itemView.findViewById(R.id.image_property)
        val textTitle: TextView = itemView.findViewById(R.id.text_title)
        val textPrice: TextView = itemView.findViewById(R.id.text_price)
        val textDetails: TextView = itemView.findViewById(R.id.text_details)
        val textArea: TextView = itemView.findViewById(R.id.text_area)
        val textRooms: TextView = itemView.findViewById(R.id.text_rooms)
        val textLocation: TextView = itemView.findViewById(R.id.text_location)
        val buttonEdit: Button = itemView.findViewById(R.id.button_edit)
        val buttonDelete: Button = itemView.findViewById(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val property = propertyList[position]

        holder.textTitle.text = property.title
        holder.textPrice.text = formatPrice(property.price)
        holder.textDetails.text = property.details
        holder.textArea.text = formatArea(property.area)
        holder.textRooms.text = property.rooms
        holder.textLocation.text = property.location

        Glide.with(holder.itemView.context)
            .load(property.imageUrl)
            .placeholder(R.drawable.ic_home)
            .into(holder.imageProperty)

        holder.buttonDelete.setOnClickListener { onDeleteClick(property) }
        holder.buttonEdit.setOnClickListener { onEditClick(property) }
    }

    override fun getItemCount(): Int = propertyList.size

    fun updateData(newList: List<Property>) {
        propertyList.clear()
        propertyList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatPrice(price: String): String {
        return if (!price.contains("₺")) {
            price.toIntOrNull()?.let {
                String.format("%,d ₺", it).replace(',', '.')
            } ?: "$price ₺"
        } else {
            price
        }
    }

    private fun formatArea(area: String): String {
        return if (!area.contains("m²")) {
            "$area m²"
        } else {
            area
        }
    }
}


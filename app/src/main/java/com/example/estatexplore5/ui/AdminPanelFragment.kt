package com.example.estatexplore5.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.estatexplore5.R
import com.example.estatexplore5.ui.home.Property
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminPropertyAdapter
    private val propertyList = mutableListOf<Property>()

    private var userEmail: String? = null
    private var userRole: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_panel, container, false)

        recyclerView = view.findViewById(R.id.recycler_admin_properties)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdminPropertyAdapter(
            propertyList,
            onDeleteClick = { property -> deleteProperty(property) },
            onEditClick = { property -> showEditDialog(property) }
        )

        recyclerView.adapter = adapter

        userEmail = requireActivity().intent.getStringExtra("email")
        userRole = requireActivity().intent.getStringExtra("role")

        loadProperties()

        return view
    }

    private fun loadProperties() {
        val firestore = FirebaseFirestore.getInstance()

        val query = if (userRole == "admin") {
            firestore.collection("properties")
        } else {
            firestore.collection("properties")
                .whereEqualTo("user", userEmail)
        }

        query.get()
            .addOnSuccessListener { result ->
                propertyList.clear()
                for (document in result) {
                    val property = document.toObject(Property::class.java)
                    property.id = document.id
                    propertyList.add(property)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load properties", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProperty(property: Property) {
        FirebaseFirestore.getInstance().collection("properties")
            .document(property.id ?: return)
            .delete()
            .addOnSuccessListener {
                propertyList.remove(property)
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditDialog(property: Property) {
        val context = requireContext()

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        val padding = (16 * resources.displayMetrics.density).toInt()
        layout.setPadding(padding, padding, padding, padding)

        val titleInput = EditText(context).apply {
            hint = "Title"
            setText(property.title)
        }
        val priceInput = EditText(context).apply {
            hint = "Price"
            setText(property.price)
        }
        val detailsInput = EditText(context).apply {
            hint = "Details"
            setText(property.details)
        }
        val categoriesInput = EditText(context).apply {
            hint = "Categories (comma separated)"
            setText(property.categories?.joinToString(", "))
        }
        val areaInput = EditText(context).apply {
            hint = "Area"
            setText(property.area)
        }
        val contactInput = EditText(context).apply {
            hint = "Contact (email or phone)"
            setText(property.contact)
        }

        val roomsInput = EditText(context).apply {
            hint = "Rooms"
            setText(property.rooms)
        }
        val locationInput = EditText(context).apply {
            hint = "Location"
            setText(property.location)
        }

        layout.addView(titleInput)
        layout.addView(priceInput)
        layout.addView(detailsInput)
        layout.addView(categoriesInput)
        layout.addView(areaInput)
        layout.addView(roomsInput)
        layout.addView(locationInput)
        layout.addView(contactInput)


        AlertDialog.Builder(context)
            .setTitle("Edit Property")
            .setView(layout)
            .setPositiveButton("Update") { dialog, _ ->
                val newTitle = titleInput.text.toString().trim()
                val newPrice = priceInput.text.toString().trim()
                val newDetails = detailsInput.text.toString().trim()
                val newCategories = categoriesInput.text.toString()
                    .split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val newArea = areaInput.text.toString().trim()
                val newRooms = roomsInput.text.toString().trim()
                val newLocation = locationInput.text.toString().trim()
                val newContact = contactInput.text.toString().trim()


                if (newTitle.isEmpty() || newPrice.isEmpty() || newDetails.isEmpty()) {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                updateProperty(property, newTitle, newPrice, newDetails, newCategories, newArea, newRooms, newLocation, newContact)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun updateProperty(
        property: Property,
        newTitle: String,
        newPrice: String,
        newDetails: String,
        newCategories: List<String>,
        newArea: String,
        newRooms: String,
        newLocation: String,
        newContact: String
    ) {
        val updatedData = mapOf(
            "title" to newTitle,
            "price" to newPrice,
            "details" to newDetails,
            "categories" to newCategories,
            "area" to newArea,
            "rooms" to newRooms,
            "location" to newLocation,
            "contact" to newContact
        )

        FirebaseFirestore.getInstance()
            .collection("properties")
            .document(property.id ?: return)
            .update(updatedData)
            .addOnSuccessListener {
                property.title = newTitle
                property.price = newPrice
                property.details = newDetails
                property.categories = newCategories
                property.area = newArea
                property.rooms = newRooms
                property.location = newLocation
                property.contact = newContact
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
            }
    }
}
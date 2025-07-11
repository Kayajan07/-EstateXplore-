package com.example.estatexplore5.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.estatexplore5.databinding.FragmentAddPropertyBinding
import com.example.estatexplore5.ui.home.Property
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.InputStream

class AddPropertyFragment : Fragment() {

    private lateinit var binding: FragmentAddPropertyBinding
    private val firestore = FirebaseFirestore.getInstance()

    private val categoriesArray = arrayOf(
        "pet", "student", "professional", "sports", "retiree",
        "family", "investment", "garden", "luxury", "single", "vacation", "minimalist"
    )

    private val selectedCategories = mutableListOf<String>()
    private val selectedBooleans = BooleanArray(categoriesArray.size)

    private val selectedImageUris = ArrayList<Uri>()

    interface ImgurApi {
        @FormUrlEncoded
        @POST("3/image")
        suspend fun uploadImage(
            @Header("Authorization") auth: String,
            @Field("image") imageBase64: String
        ): ImgurResponse
    }

    data class ImgurResponse(val data: ImgurData)
    data class ImgurData(val link: String)

    private val imgurClientId = "Client-ID df621cd75670a8c"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false)

        binding.buttonSelectImage.setOnClickListener {
            pickImagesFromGallery()
        }

        binding.buttonAddProperty.setOnClickListener {
            uploadAllImagesAndProperty()
        }

        binding.buttonSelectCategories.setOnClickListener {
            showCategorySelectionDialog()
        }

        return binding.root
    }

    private fun showCategorySelectionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Select Categories")
            .setMultiChoiceItems(categoriesArray, selectedBooleans) { _, index, isChecked ->
                if (isChecked) selectedCategories.add(categoriesArray[index])
                else selectedCategories.remove(categoriesArray[index])
            }
            .setPositiveButton("OK") { dialog, _ ->
                binding.editTextCategories.setText(selectedCategories.joinToString(", "))
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun pickImagesFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUris.clear()

            if (data.clipData != null) {
                for (i in 0 until data.clipData!!.itemCount) {
                    selectedImageUris.add(data.clipData!!.getItemAt(i).uri)
                }
            } else if (data.data != null) {
                selectedImageUris.add(data.data!!)
            }

            if (selectedImageUris.isNotEmpty()) {
                Glide.with(this).load(selectedImageUris[0]).into(binding.imagePreview)
            }
        }
    }

    private fun uploadAllImagesAndProperty() {
        val title = binding.editTextTitle.text.toString().trim()
        val rawPrice = binding.editTextPrice.text.toString().trim()
        val rawArea = binding.editTextArea.text.toString().trim()
        val rooms = binding.editTextRooms.text.toString().trim()
        val location = binding.editTextLocation.text.toString().trim()
        val contact = binding.editTextContact.text.toString().trim()
        val details = binding.editTextDetails.text.toString().trim()
        val categories = binding.editTextCategories.text.toString()
            .split(",").map { it.trim().lowercase() }

        if (title.isEmpty() || rawPrice.isEmpty() || rawArea.isEmpty() || rooms.isEmpty()
            || location.isEmpty() || details.isEmpty() || selectedImageUris.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Please fill all fields and select images", Toast.LENGTH_SHORT).show()
            return
        }

        val formattedPrice = formatPrice(rawPrice)
        val formattedArea = formatArea(rawArea)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allImageUrls = selectedImageUris.map { uri ->
                    uploadImageToImgurSuspending(uri)
                }

                val mainImageUrl = allImageUrls.first()
                val extraImageUrls = allImageUrls.drop(1)

                Log.d("UPLOAD", "Main image: $mainImageUrl")
                Log.d("UPLOAD", "Extra images: $extraImageUrls")

                savePropertyToFirestore(
                    mainImageUrl,
                    extraImageUrls,
                    title,
                    formattedPrice,
                    details,
                    categories,
                    formattedArea,
                    rooms,
                    location,
                    contact
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Upload error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun uploadImageToImgurSuspending(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val bytes = inputStream?.use { it.readBytes() }
            val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(ImgurApi::class.java)
            val response = api.uploadImage(imgurClientId, base64Image)
            response.data.link
        }
    }

    private fun savePropertyToFirestore(
        imageUrl: String,
        extraImages: List<String>,
        title: String,
        price: String,
        details: String,
        categories: List<String>,
        area: String,
        rooms: String,
        location: String,
        contact: String
    ) {
        val userEmail = requireActivity().intent.getStringExtra("email") ?: "unknown"
        val property = Property(
            imageUrl = imageUrl,
            extraImages = extraImages,
            title = title,
            price = price,
            details = details,
            categories = categories,
            user = userEmail,
            area = area,
            rooms = rooms,
            location = location,
            contact = contact
        )

        firestore.collection("properties")
            .add(property)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Property added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error adding property", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatPrice(price: String): String {
        return try {
            val cleanPrice = price.replace("\\D".toRegex(), "")
            val formatted = "%,d".format(cleanPrice.toLong()).replace(",", ".")
            "$formatted ₺"
        } catch (e: Exception) {
            price
        }
    }

    private fun formatArea(area: String): String {
        return try {
            val cleanArea = area.replace("\\D".toRegex(), "")
            cleanArea
        } catch (e: Exception) {
            area
        }
    }
}




package com.example.estatexplore5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.estatexplore5.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.estatexplore5.ui.home.Property
import com.example.estatexplore5.ui.home.PropertyDetailFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchPropertiesFromFirestore()

        binding.buttonSearch.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PropertiesFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchPropertiesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("properties")
            .get()
            .addOnSuccessListener { result ->
                binding.layoutRecommended.removeAllViews()
                val inflater = LayoutInflater.from(requireContext())

                for (document in result) {
                    val property = document.toObject(Property::class.java)
                    val cardView = inflater.inflate(R.layout.card_recommended, binding.layoutRecommended, false)

                    val image = cardView.findViewById<ImageView>(R.id.image_property)
                    val title = cardView.findViewById<TextView>(R.id.text_title)
                    val price = cardView.findViewById<TextView>(R.id.text_price)
                    val details = cardView.findViewById<TextView>(R.id.text_details)

                    Glide.with(this)
                        .load(property.imageUrl)
                        .into(image)

                    title.text = property.title
                    price.text = formatPrice(property.price)
                    details.text = property.location

                    cardView.setOnClickListener {
                        val fragment = PropertyDetailFragment()
                        val bundle = Bundle().apply {
                            putString("title", property.title)
                            putString("price", formatPrice(property.price))
                            putString("details", property.details)
                            putString("imageUrl", property.imageUrl)
                            putString("area", property.area)
                            putString("rooms", property.rooms)
                            putString("location", property.location)
                            putString("contact", property.contact)
                            putString("categories", property.categories.joinToString(", "))
                            putStringArrayList("extraImages", ArrayList(property.extraImages))
                        }
                        fragment.arguments = bundle

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    }

                    binding.layoutRecommended.addView(cardView)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





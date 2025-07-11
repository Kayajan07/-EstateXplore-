package com.example.estatexplore5.ui
import com.example.estatexplore5.ui.home.PropertyAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.estatexplore5.databinding.FragmentFavoritesBinding
import com.example.estatexplore5.ui.home.Property
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: PropertyAdapter
    private val favoritesList = mutableListOf<Property>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        adapter = PropertyAdapter(favoritesList)
        binding.recyclerFavorites.adapter = adapter

        loadFavorites()

        return binding.root
    }

    private fun loadFavorites() {
        val userEmail = requireActivity().intent.getStringExtra("email") ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("favorites")
            .document(userEmail)
            .collection("properties")
            .get()
            .addOnSuccessListener { result ->
                favoritesList.clear()
                for (document in result) {
                    val property = document.toObject(Property::class.java)
                    favoritesList.add(property)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
            }
    }
}


package com.example.estatexplore5

import com.example.estatexplore5.ui.home.Property
import com.example.estatexplore5.ui.FilterFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.estatexplore5.ui.home.PropertyAdapter
import com.google.firebase.firestore.FirebaseFirestore

class PropertiesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var propertyAdapter: PropertyAdapter

    private val propertyList = mutableListOf<Property>()
    private val allProperties = mutableListOf<Property>()
    private var selectedFilters: ArrayList<String> = arrayListOf()
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selectedFilters = arguments?.getStringArrayList("selectedFilters") ?: arrayListOf()

        val view = inflater.inflate(R.layout.fragment_properties, container, false)
        recyclerView = view.findViewById(R.id.recycler_properties)
        searchView = view.findViewById(R.id.search_view)

        // ⬇⬇⬇ Filtre butonuna tıklanınca FilterFragment’a git ⬇⬇⬇
        view.findViewById<View>(R.id.button_filter).setOnClickListener {
            val fragment = FilterFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propertyAdapter = PropertyAdapter(propertyList)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = propertyAdapter

        fetchPropertiesFromFirestore()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                applySearchAndFilters(currentQuery)
                return true
            }
        })
    }

    private fun fetchPropertiesFromFirestore() {
        FirebaseFirestore.getInstance().collection("properties")
            .get()
            .addOnSuccessListener { result ->
                allProperties.clear()
                for (document in result) {
                    val property = document.toObject(Property::class.java)
                    allProperties.add(property)
                }
                applySearchAndFilters(currentQuery)
            }
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    private fun applySearchAndFilters(query: String) {
        val filteredByCategory = if (selectedFilters.isNotEmpty()) {
            allProperties.filter { property ->
                val categories = property.categories ?: emptyList()
                // 🔥 Tüm filtreler bu evin kategorileri içinde var mı?
                categories.map { it.lowercase() }.containsAll(
                    selectedFilters.map { it.lowercase() }
                )
            }
        } else {
            allProperties
        }

        val result = filteredByCategory.filter { property ->
            property.title.contains(query, ignoreCase = true) ||
                    property.details.contains(query, ignoreCase = true)
        }

        propertyList.clear()
        propertyList.addAll(result)
        propertyAdapter.notifyDataSetChanged()
    }
}

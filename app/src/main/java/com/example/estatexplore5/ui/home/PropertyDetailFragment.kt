package com.example.estatexplore5.ui.home
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.text.style.ClickableSpan
import android.text.method.LinkMovementMethod
import android.text.Spanned

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.estatexplore5.R
import com.example.estatexplore5.databinding.FragmentPropertyDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class PropertyDetailFragment : Fragment() {

    private var _binding: FragmentPropertyDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageAdapter: PropertyImageAdapter

    private val categoryDescriptions = mapOf(
        "pet" to "✔ Pets are allowed — ideal for pet owners seeking a welcoming home.",
        "student" to "✔ Tailored for students — affordable, practical, and close to key amenities.",
        "professional" to "✔ Suitable for professionals — refined and efficient living environment.",
        "sports" to "✔ Close to sports facilities — perfect for active and athletic lifestyles.",
        "retiree" to "✔ Calm and accessible — designed with retirees in mind.",
        "family" to "✔ Family-oriented — spacious layout in a secure neighborhood.",
        "investment" to "✔ Great investment potential — located in a growing area.",
        "garden" to "✔ Includes access to a private or shared garden space.",
        "luxury" to "✔ High-end finishes — premium amenities and elegant design.",
        "single" to "✔ Ideal for individuals — compact, stylish, and low-maintenance.",
        "vacation" to "✔ Vacation-ready — great for seasonal living or holiday getaways.",
        "minimalist" to "✔ Minimalist concept — sleek, modern, and clutter-free."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyDetailBinding.inflate(inflater, container, false)

        val args = arguments
        val title = args?.getString("title") ?: ""
        val price = args?.getString("price") ?: ""
        val details = args?.getString("details") ?: ""
        val imageUrl = args?.getString("imageUrl") ?: ""
        val extraImages = args?.getStringArrayList("extraImages") ?: arrayListOf()
        val area = args?.getString("area") ?: ""
        val rooms = args?.getString("rooms") ?: ""
        val location = args?.getString("location") ?: ""
        val contact = args?.getString("contact") ?: ""
        val categoriesRaw = args?.getString("categories") ?: ""

        val allImages = ArrayList<String>()
        allImages.add(imageUrl)
        allImages.addAll(extraImages)

        imageAdapter = PropertyImageAdapter(allImages)
        binding.viewPagerImages.adapter = imageAdapter
        binding.viewPagerImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.textTitle.text = title
        binding.textPrice.text = price

        // Açıklama metni inşa bloğu
        val detailsText = SpannableStringBuilder()

// 📝 Description
        if (details.isNotEmpty()) {
            detailsText.append("📝 Description:\n$details\n\n")
        }

// 📐 Property Details
        if (area.isNotEmpty() || rooms.isNotEmpty() || location.isNotEmpty()) {
            detailsText.append("📐 Property Details:\n")
            if (area.isNotEmpty()) detailsText.append("• Area: $area m²\n")
            if (rooms.isNotEmpty()) detailsText.append("• Rooms: $rooms\n")
            if (location.isNotEmpty()) detailsText.append("• Location: $location\n\n")
        }

// 📞 Contact Information
        if (contact.isNotEmpty()) {
            detailsText.append("📞 Contact Information:\n")

            val phoneText = "• $contact"
            val start = detailsText.length
            detailsText.append(phoneText)
            val end = detailsText.length

            // ALTINI ÇİZ ve TIKLANABİLİR yap
            detailsText.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            detailsText.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$contact")
                    }
                    startActivity(intent)
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Altına satır atmak istiyorsan burada yap
            detailsText.append("\n\n")
        }

// 🏷️ Key Features
        val selectedCategories = categoriesRaw.split(",").map { it.trim() }
        val descriptionLines = selectedCategories.mapNotNull { categoryDescriptions[it] }
        if (descriptionLines.isNotEmpty()) {
            detailsText.append("🏷️ Key Features:\n")
            descriptionLines.forEach {
                detailsText.append("• $it\n")
            }
        }

        binding.textDetails.text = detailsText
        binding.textDetails.movementMethod = LinkMovementMethod.getInstance()

        val property = Property(
            title = title,
            price = price,
            details = details,
            imageUrl = imageUrl,
            extraImages = extraImages,
            categories = selectedCategories,
            user = "",
            area = area,
            rooms = rooms,
            location = location,
            contact = contact
        )

        checkFavoriteStatus(property)

        binding.imageFavorite.setOnClickListener {
            toggleFavorite(property)
        }

        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleFavorite(property: Property) {
        val userEmail = requireActivity().intent.getStringExtra("email") ?: return
        val favRef = FirebaseFirestore.getInstance()
            .collection("favorites")
            .document(userEmail)
            .collection("properties")
            .document(property.title)

        favRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                favRef.delete().addOnSuccessListener {
                    binding.imageFavorite.setImageResource(R.drawable.ic_favorite_border_mor)
                }
            } else {
                favRef.set(property).addOnSuccessListener {
                    binding.imageFavorite.setImageResource(R.drawable.ic_favorite_filled_mor)
                }
            }
        }
    }

    private fun checkFavoriteStatus(property: Property) {
        val userEmail = requireActivity().intent.getStringExtra("email") ?: return
        val favRef = FirebaseFirestore.getInstance()
            .collection("favorites")
            .document(userEmail)
            .collection("properties")
            .document(property.title)

        favRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                binding.imageFavorite.setImageResource(R.drawable.ic_favorite_filled_mor)
            } else {
                binding.imageFavorite.setImageResource(R.drawable.ic_favorite_border_mor)
            }
        }
    }
}









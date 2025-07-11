package com.example.estatexplore5

data class Property(
    var id: String? = null,
    val imageUrl: String = "",                      // Kapak fotoğrafı (ilk seçilen)
    val extraImages: List<String> = emptyList(),           // Diğer ekstra fotoğraflar
    var title: String = "",
    var price: String = "",
    var details: String = "",
    var user: String = "",
    var categories: List<String>? = null,
    var area: String = "",
    var rooms: String = "",
    var contact: String = "",
    var location: String = ""
)




package com.example.estatexplore5.ui

import com.example.estatexplore5.R
import com.example.estatexplore5.PropertiesFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment

class FilterFragment : Fragment() {

    private lateinit var checkboxPet: CheckBox
    private lateinit var checkboxStudent: CheckBox
    private lateinit var checkboxProfessional: CheckBox
    private lateinit var checkboxSports: CheckBox
    private lateinit var checkboxRetiree: CheckBox
    private lateinit var checkboxFamily: CheckBox
    private lateinit var checkboxInvestment: CheckBox
    private lateinit var checkboxGarden: CheckBox
    private lateinit var checkboxLuxury: CheckBox
    private lateinit var checkboxSingle: CheckBox
    private lateinit var checkboxVacation: CheckBox
    private lateinit var checkboxMinimalist: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)

        checkboxPet = view.findViewById(R.id.checkbox_pet_friendly)
        checkboxStudent = view.findViewById(R.id.checkbox_student)
        checkboxProfessional = view.findViewById(R.id.checkbox_professional)
        checkboxSports = view.findViewById(R.id.checkbox_sports)
        checkboxRetiree = view.findViewById(R.id.checkbox_retiree)
        checkboxFamily = view.findViewById(R.id.checkbox_family)
        checkboxInvestment = view.findViewById(R.id.checkbox_investment)
        checkboxGarden = view.findViewById(R.id.checkbox_garden)
        checkboxLuxury = view.findViewById(R.id.checkbox_luxury)
        checkboxSingle = view.findViewById(R.id.checkbox_single)
        checkboxVacation = view.findViewById(R.id.checkbox_vacation)
        checkboxMinimalist = view.findViewById(R.id.checkbox_minimalist)

        view.findViewById<View>(R.id.button_apply_filters).setOnClickListener {
            val selectedFilters = mutableListOf<String>()

            if (checkboxPet.isChecked) selectedFilters.add("pet")
            if (checkboxStudent.isChecked) selectedFilters.add("student")
            if (checkboxProfessional.isChecked) selectedFilters.add("professional")
            if (checkboxSports.isChecked) selectedFilters.add("sports")
            if (checkboxRetiree.isChecked) selectedFilters.add("retiree")
            if (checkboxFamily.isChecked) selectedFilters.add("family")
            if (checkboxInvestment.isChecked) selectedFilters.add("investment")
            if (checkboxGarden.isChecked) selectedFilters.add("garden")
            if (checkboxLuxury.isChecked) selectedFilters.add("luxury")
            if (checkboxSingle.isChecked) selectedFilters.add("single")
            if (checkboxVacation.isChecked) selectedFilters.add("vacation")
            if (checkboxMinimalist.isChecked) selectedFilters.add("minimalist")

            val propertiesFragment = PropertiesFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList("selectedFilters", ArrayList(selectedFilters))
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, propertiesFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}



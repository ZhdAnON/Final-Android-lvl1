package ru.zhdanon.skillcinema.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.slider.RangeSlider
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.databinding.FragmentSearchSettingsBinding

class FragmentSearchSettings : Fragment() {
    private var _binding: FragmentSearchSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchSettingsBackBtn.setOnClickListener { requireActivity().onBackPressed() }

        setRatingSlider()
    }

    private fun setRatingSlider() {
        binding.searchSettingsRatingSlider.values = listOf(1f, 10f)
        binding.searchSettingsRatingSlider.addOnSliderTouchListener(object :
            RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
                if (slider.values == listOf(1f, 10f)) {
                    binding.searchSettingsRatingTv.text = "любой"
                } else {
                    val values = slider.values.map { it.toInt() }
                    binding.searchSettingsRatingTv.text =
                        resources.getString(
                            R.string.search_settings_rating_text,
                            values[0],
                            values[1]
                        )
                }
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                if (slider.values == listOf(1f, 10f)) {
                    binding.searchSettingsRatingTv.text = "любой"
                } else {
                    val values = slider.values.map { it.toInt() }
                    binding.searchSettingsRatingTv.text =
                        resources.getString(
                            R.string.search_settings_rating_text,
                            values[0],
                            values[1]
                        )
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package ru.zhdanon.skillcinema.ui.intro

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.databinding.IntroFragmentBinding

class IntroFragment : Fragment() {

    private var _binding: IntroFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: IntroPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = IntroFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = IntroPagerAdapter(parentFragmentManager) { skipIntroClick() }
        binding.introViewpager.adapter = adapter

        binding.btnIntroSkip.setOnClickListener { skipIntroClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().clearBackStack(R.id.introFragment)
        _binding = null
    }

    private fun skipIntroClick() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putBoolean(PREFERENCES_NAME, true)
            apply()
        }
        findNavController().navigate(R.id.action_introFragment_to_mainFragment)
    }

    companion object {
        const val PREFERENCES_NAME = "pref_name"
    }
}
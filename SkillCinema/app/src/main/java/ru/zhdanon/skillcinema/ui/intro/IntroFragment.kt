package ru.zhdanon.skillcinema.ui.intro

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.databinding.IntroFragmentBinding
import ru.zhdanon.skillcinema.ui.MainFragment
import ru.zhdanon.skillcinema.ui.TAG

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
        _binding = null
    }

    private fun skipIntroClick() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putBoolean(PREFERENCES_NAME, true)
            apply()
        }
        Log.d(TAG, "skipIntroClick: ")
//        findNavController().navigate(R.id.action_introFragment_to_mainFragment)
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, MainFragment())
            .commit()
    }

    companion object {
        const val PREFERENCES_NAME = "pref_name"
    }
}
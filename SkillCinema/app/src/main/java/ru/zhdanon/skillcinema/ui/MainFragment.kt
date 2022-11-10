package ru.zhdanon.skillcinema.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.databinding.FragmentMainBinding
import ru.zhdanon.skillcinema.ui.home.FragmentHome
import ru.zhdanon.skillcinema.ui.profile.FragmentProfile
import ru.zhdanon.skillcinema.ui.search.FragmentSearch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
            .beginTransaction()
            .replace(R.id.working_container, FragmentHome())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(FragmentHome())
                    true
                }
                R.id.search -> {
                    loadFragment(FragmentSearch())
                    true
                }
                else -> {
                    loadFragment(FragmentProfile())
                    true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.working_container, fragment)
            .commit()
    }
}
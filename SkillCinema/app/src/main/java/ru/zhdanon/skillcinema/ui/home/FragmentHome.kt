package ru.zhdanon.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.data.CategoriesFilms
import ru.zhdanon.skillcinema.databinding.FragmentHomeBinding
import ru.zhdanon.skillcinema.ui.CinemaViewModel
import ru.zhdanon.skillcinema.ui.StateLoading
import ru.zhdanon.skillcinema.ui.home.categoryrecycler.CategoryAdapter

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CinemaViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateLoadingListener()

        getCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homePageList.collect {
                categoryAdapter =
                    CategoryAdapter(20, it, { onClickShoAllButton(it) }, { onClickFilm(it) })
                binding.categoryList.adapter = categoryAdapter
            }
        }
    }

    private fun onClickFilm(filmId: Int) {
        val action =
            FragmentHomeDirections.actionFragmentHomeToFragmentFilmDetail(KEY_NAV_FRAGMENT_HOME)
        viewModel.getFilmById(filmId)
        findNavController().navigate(action)
    }

    private fun onClickShoAllButton(category: CategoriesFilms) {
        viewModel.setCurrentCategory(category)
        findNavController().navigate(R.id.action_fragmentHome_to_fragmentAllFilms)
    }

    private fun stateLoadingListener() {
        viewModel.loadCategoryState.onEach { state ->
            when (state) {
                is StateLoading.Loading -> {
                    binding.progressGroup.isVisible = true
                    binding.loadingProgress.isVisible = true
                    binding.loadingRefreshBtn.isVisible = false
                    binding.categoryList.isVisible = false
                }
                is StateLoading.Success -> {
                    binding.progressGroup.isVisible = false
                    binding.categoryList.isVisible = true
                }
                else -> {
                    binding.progressGroup.isVisible = true
                    binding.loadingProgress.isVisible = false
                    binding.loadingRefreshBtn.isVisible = true
                    binding.categoryList.isVisible = false
                    binding.loadingRefreshBtn.setOnClickListener { viewModel.getAllFilms() }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    companion object {
        const val KEY_NAV_FRAGMENT_HOME = "Fragment Home"
    }
}
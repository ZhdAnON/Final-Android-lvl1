package ru.zhdanon.skillcinema.ui.allfilmsbycategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.databinding.FragmentAllFilmsBinding
import ru.zhdanon.skillcinema.ui.CinemaViewModel
import ru.zhdanon.skillcinema.ui.allfilmsbycategory.allfilmadapter.AllFilmAdapter

class FragmentAllFilms : Fragment() {
    private var _binding: FragmentAllFilmsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CinemaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllFilmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.allFilmsCategoryTv.text = viewModel.getCurrentCategory().text
        viewModel.setAllFilmAdapter(AllFilmAdapter { onClickFilm(it) })

        binding.allFilmsList.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.allFilmsList.adapter = viewModel.getAllFilmAdapter()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.allFilmsByCategory.collect {
                viewModel.getAllFilmAdapter().submitData(it)
            }
        }

        binding.allFilmsToHomeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClickFilm(filmId: Int) {
        viewModel.getFilmById(filmId)
        findNavController().navigate(R.id.action_fragmentAllFilms_to_fragmentFilmDetail)
    }
}
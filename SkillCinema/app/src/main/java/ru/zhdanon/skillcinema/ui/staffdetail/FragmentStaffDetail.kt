package ru.zhdanon.skillcinema.ui.staffdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.app.loadImage
import ru.zhdanon.skillcinema.databinding.FragmentStaffDetailBinding
import ru.zhdanon.skillcinema.entity.HomeItem
import ru.zhdanon.skillcinema.ui.StateLoading
import ru.zhdanon.skillcinema.ui.TAG
import ru.zhdanon.skillcinema.ui.home.filmrecycler.FilmAdapter

class FragmentStaffDetail : Fragment() {
    private var _binding: FragmentStaffDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StaffViewModel by activityViewModels()
    private lateinit var filmAdapter: FilmAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: FragmentStaffDetailArgs by navArgs()
        viewModel.getStaffDetail(args.staffId)

        setBestFilmsAdapter()

        setLoadingStateAndDetails()
        getStaffInfo()

        binding.staffDetailBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentStaffDetail_to_fragmentFilmDetail)
        }

        binding.staffDetailShowAllFilmsBtn.setOnClickListener { getAllFilmsByStaff() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBestFilmsAdapter() {
        filmAdapter = FilmAdapter(20, {}, {})
        binding.staffDetailBestList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.staffDetailBestList.adapter = filmAdapter
    }

    private fun setLoadingStateAndDetails() {
        viewModel.loadCurrentStaff.onEach { state ->
            when (state) {
                is StateLoading.Loading -> {
                    binding.apply {
                        progressGroup.isVisible = true
                        loadingRefreshBtn.isVisible = false
                        staffDetailMainGroup.isVisible = false
                        staffDetailBestGroup.isVisible = false
                        staffDetailFilmographyGroup.isVisible = false
                    }
                }
                is StateLoading.Success -> {
                    binding.apply {
                        progressGroup.isVisible = false
                        loadingRefreshBtn.isVisible = false
                        staffDetailMainGroup.isVisible = true
                        staffDetailBestGroup.isVisible = true
                        staffDetailFilmographyGroup.isVisible = true
                    }
                }
                else -> {
                    binding.apply {
                        progressGroup.isVisible = false
                        loadingRefreshBtn.isVisible = true
                        staffDetailMainGroup.isVisible = false
                        staffDetailBestGroup.isVisible = false
                        staffDetailFilmographyGroup.isVisible = false
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun getStaffInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentStaff.collect { staff ->
                binding.apply {
                    staffDetailPoster.loadImage(staff.posterUrl)
                    staffDetailName.text = staff.nameRu ?: staff.nameEn ?: "Unknown name"
                    if (staff.profession != null) staffDetailProfession.text = staff.profession
                    else staffDetailProfession.isVisible = false

                    if (staff.films != null) staffDetailFilmsCount.text =
                        resources.getQuantityString(
                            R.plurals.staff_details_film_count,
                            staff.films.size,
                            staff.films.size
                        )
                    if (staff.films != null) {
                        staff.films.map {
                            Log.d(TAG, "staff рейтинг - ${it.rating}")
                        }
                        val list: MutableList<HomeItem> = staff.films.toMutableList()
                        list.removeAll { it.rating == null }
                        val sortedList = list.sortedBy { it.rating?.toDouble() }.reversed()
                        val result = mutableListOf<HomeItem>()
                        if (sortedList.size > 10) {
                            repeat(10) {
                                result.add(sortedList[it])
                            }
                        } else result.addAll(sortedList)
                        result.sortedBy { it.rating }
                        result.map {
                            Log.d(TAG, "result рейтинг - ${it.rating}")
                        }
                        filmAdapter.submitList(result)
                    }
                }
            }
        }
    }

    private fun getAllFilmsByStaff() {

    }
}
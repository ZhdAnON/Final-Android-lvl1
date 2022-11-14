package ru.zhdanon.skillcinema.ui.staffdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.zhdanon.skillcinema.data.staffbyid.ResponseStaffById
import ru.zhdanon.skillcinema.domain.GetStaffByIdUseCase
import ru.zhdanon.skillcinema.ui.StateLoading
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val getStaffByIdUseCase: GetStaffByIdUseCase
) : ViewModel() {

    private val _currentStaff = MutableSharedFlow<ResponseStaffById>()
    val currentStaff = _currentStaff.asSharedFlow()

    private val _loadCurrentStaff = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCurrentStaff = _loadCurrentStaff.asStateFlow()

    fun getStaffDetail(staffId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadCurrentStaff.value = StateLoading.Loading
                val staff = getStaffByIdUseCase.executeStaffById(staffId)
                _currentStaff.emit(staff)
                _loadCurrentStaff.value = StateLoading.Success
            } catch (e: Exception) {
                _loadCurrentStaff.value = StateLoading.Error(e.message.toString())
            }
        }
    }
}
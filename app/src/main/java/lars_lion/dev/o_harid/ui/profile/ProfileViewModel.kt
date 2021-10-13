package lars_lion.dev.o_harid.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.getMoney.GetUserMoneyResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _userMoney = MutableLiveData<Event<UiState<GetUserMoneyResponse>>>()
    val userMoney: LiveData<Event<UiState<GetUserMoneyResponse>>> = _userMoney

    fun getUserMoney() = viewModelScope.launch {
        _userMoney.value = Event(UiState.Loading)
        try {
            repository.getUserMoney().catch { e ->
                Event(UiState.Error("main error ->${e.message}"))
            }.collectLatest { response ->
                if (response.status.code == 200)
                    _userMoney.value = Event(UiState.Success(response))
                else _userMoney.value = Event(UiState.Error(response.status.message.toString()))
            }
        } catch (e: Exception) {
            _userMoney.value = Event(UiState.Error("main error ->${e.message}"))
        }
    }
}
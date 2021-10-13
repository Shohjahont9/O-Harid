package lars_lion.dev.o_harid.ui.registration.reg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.login.LoginResponse
import lars_lion.dev.o_harid.network.response.register.RegisterResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: RegisstrationRepository
) : ViewModel() {
    private val _register = MutableLiveData<Event<UiState<RegisterResponse>>>()
    val register: LiveData<Event<UiState<RegisterResponse>>> = _register

    fun registerUser(body: String) = viewModelScope.launch {
        _register.value = Event(UiState.Loading)
        try {
            repository.register(body).catch { e ->
                _register.value =
                    Event(UiState.Error("register User error -> ${e.message.toString()}"))
            }.collectLatest { response ->
                if (response.status.code == 200)
                    _register.value = Event(UiState.Success(response))
                else
                    _register.value = Event(UiState.Error(response.status.message))
            }
        } catch (e: Exception) {
            _register.value = Event(UiState.Error("register User error -> ${e.message.toString()}"))
        }
    }
}
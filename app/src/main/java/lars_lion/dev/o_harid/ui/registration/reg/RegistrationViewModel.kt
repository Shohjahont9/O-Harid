package lars_lion.dev.o_harid.ui.registration.reg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.register.RegisterResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState

@HiltViewModel
class RegistrationViewModel(
    private val repository: RegisstrationRepository
) : ViewModel() {
    private val _register = MutableLiveData<Event<UiState<RegisterResponse>>>()
    val register: LiveData<Event<UiState<RegisterResponse>>> = _register

    fun registerUser(body: String) = viewModelScope.launch {
        _register.value = Event(UiState.Loading)
        try {
            when (repository.register(body).code()) {
                200 -> _register.value = Event(UiState.Success(repository.register(body).body()!!))
                202 -> _register.value = Event(UiState.Error("Bunday raqam mavjud emas!"))
            }
        } catch (e: Exception) {
            _register.value = Event(UiState.Error("register User error -> ${e.message.toString()}"))
        }
    }
}
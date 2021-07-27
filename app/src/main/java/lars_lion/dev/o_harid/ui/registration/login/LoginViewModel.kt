package lars_lion.dev.o_harid.ui.registration.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.login.LoginResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _login = MutableLiveData<Event<UiState<LoginResponse>>>()
    val login: LiveData<Event<UiState<LoginResponse>>> = _login

    fun loginUser(response: LoginResponse) = viewModelScope.launch {
        _login.value = Event(UiState.Loading)
        try {
           _login.value = Event(UiState.Success(response))
        } catch (e: Exception) {
            _login.value = Event(UiState.Error("login User error -> ${e.message.toString()}"))
        }
    }

    suspend fun fetchLoginUser(body:String) = repository.login(body)
}
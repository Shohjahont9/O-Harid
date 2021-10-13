package lars_lion.dev.o_harid.ui.card.verifyCard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.verifyCode.VerifyCodeResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class VerifyCardViewModel @Inject constructor(
    private val repository: VerifyCardRepository
) : ViewModel() {
    private val _verifyCode = MutableLiveData<Event<UiState<VerifyCodeResponse>>>()
    val verifyCode: LiveData<Event<UiState<VerifyCodeResponse>>> = _verifyCode

    fun getVerifyCode(code: String) = viewModelScope.launch {
        _verifyCode.value = Event(UiState.Loading)
        try {
            repository.getCode(code).catch { e ->
                _verifyCode.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _verifyCode.value = Event(UiState.Success(repos))
                else _verifyCode.value = Event(UiState.Error(repos.status.message))
            }
        } catch (e: Exception) {
            _verifyCode.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }

}
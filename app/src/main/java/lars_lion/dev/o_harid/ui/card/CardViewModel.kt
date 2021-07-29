package lars_lion.dev.o_harid.ui.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.createCard.CreateCardResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    private val _createCard = MutableLiveData<Event<UiState<CreateCardResponse>>>()
    val createCard: LiveData<Event<UiState<CreateCardResponse>>> = _createCard

    fun getCreateCard(body: String) = viewModelScope.launch {
        _createCard.value = Event(UiState.Loading)
        try {
            repository.createCard(body).catch { e ->
                _createCard.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _createCard.value = Event(UiState.Success(repos))
            }
        } catch (e: Exception) {
            _createCard.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }

}
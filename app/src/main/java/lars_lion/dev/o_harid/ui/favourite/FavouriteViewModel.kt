package lars_lion.dev.o_harid.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.favourite.FavouriteBookResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: FavouriteRepository
):ViewModel(){

    private val _favouriteBook = MutableLiveData<Event<UiState<FavouriteBookResponse>>>()
    val favouriteBook: LiveData<Event<UiState<FavouriteBookResponse>>> = _favouriteBook

    fun getFavouriteBook() = viewModelScope.launch {
        _favouriteBook.value = Event(UiState.Loading)
        try {
            repository.favouriteBook().catch {e->
                _favouriteBook.value = Event(UiState.Error(e.message ?: "message == null"))
            }.collectLatest {response->
                if (response.status.code == 200)
                    _favouriteBook.value = Event(UiState.Success(response))
            }
        }catch (e:Exception){
            _favouriteBook.value = Event(UiState.Error(e.message ?: "message == null"))
        }
    }

}
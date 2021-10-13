package lars_lion.dev.o_harid.ui.getBookByType

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.addFavourite.AddFavouriteResponse
import lars_lion.dev.o_harid.network.response.getBookByBookType.GetBookByBookTypeResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject


@HiltViewModel
class GetBookTypeViewModel @Inject constructor(
    private val repository: GetBookTypeRepository
) : ViewModel() {

    private val _getBookType = MutableLiveData<Event<UiState<GetBookByBookTypeResponse>>>()
    val getBookType: LiveData<Event<UiState<GetBookByBookTypeResponse>>> = _getBookType

    fun getBookType(typeId: String) = viewModelScope.launch {
        _getBookType.value = Event(UiState.Loading)
        try {
            repository.getBookType(typeId).catch { e ->
                _getBookType.value = Event(UiState.Error(e.message ?: "message == null"))
            }.collectLatest { response ->
                if (response.status.code == 200)
                    _getBookType.value = Event(UiState.Success(response))
            }
        } catch (e: Exception) {
            _getBookType.value = Event(UiState.Error(e.message ?: "message == null"))
        }
    }


    private val _addFavouriteBook = MutableLiveData<Event<UiState<AddFavouriteResponse>>>()
    val addFavouriteBook: LiveData<Event<UiState<AddFavouriteResponse>>> = _addFavouriteBook

    fun addFavBook(id: Int) = viewModelScope.launch {
        _addFavouriteBook.value = Event(UiState.Loading)
        try {
            repository.addFavouriteBook(id).catch { e ->
                _addFavouriteBook.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _addFavouriteBook.value = Event(UiState.Success(repos))
                else
                    _addFavouriteBook.value = Event(UiState.Error(repos.status.message))
            }
        } catch (e: Exception) {
            _addFavouriteBook.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }


}
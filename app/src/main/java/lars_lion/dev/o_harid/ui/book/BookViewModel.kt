package lars_lion.dev.o_harid.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.paidBooks.PaidBooksResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private val _paidBooks = MutableLiveData<Event<UiState<PaidBooksResponse>>>()
    val paidBooks: LiveData<Event<UiState<PaidBooksResponse>>> = _paidBooks

    fun getPaidBooks() = viewModelScope.launch {
        _paidBooks.value = Event(UiState.Loading)
        try {
            repository.paidBooks().catch { e ->
                _paidBooks.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _paidBooks.value = Event(UiState.Success(repos))
                else
                    _paidBooks.value = Event(UiState.Error(repos.status.message))
            }
        } catch (e: Exception) {
            _paidBooks.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }
}
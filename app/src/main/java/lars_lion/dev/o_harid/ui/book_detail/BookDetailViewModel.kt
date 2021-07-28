package lars_lion.dev.o_harid.ui.book_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.bookDetail.BookDetailResponse
import lars_lion.dev.o_harid.network.response.comments.CommentResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: BookDetailRepository
) : ViewModel() {

    private val _comments = MutableLiveData<Event<UiState<CommentResponse>>>()
    val comments: LiveData<Event<UiState<CommentResponse>>> = _comments

    fun getComment() = viewModelScope.launch {
        _comments.value = Event(UiState.Loading)
        try {
            repository.comments().catch {e->
                _comments.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest {repos->
                if (repos.status.code==200)
                    _comments.value = Event(UiState.Success(repos))
            }
        }catch (e:Exception){
            _comments.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }


    private val _bookDetail = MutableLiveData<Event<UiState<BookDetailResponse>>>()
    val bookDetail: LiveData<Event<UiState<BookDetailResponse>>> = _bookDetail

    fun getBookDetail(id:Int) = viewModelScope.launch {
        _bookDetail.value = Event(UiState.Loading)
        try {
            repository.bookDetail(id).catch {e->
                _bookDetail.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest {repos->
                if (repos.status.code==200)
                    _bookDetail.value = Event(UiState.Success(repos))
            }
        }catch (e:Exception){
            _bookDetail.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }


}
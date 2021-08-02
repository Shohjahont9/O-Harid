package lars_lion.dev.o_harid.ui.book_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.addComment.AddCommentResponse
import lars_lion.dev.o_harid.network.response.addFavourite.AddFavouriteResponse
import lars_lion.dev.o_harid.network.response.bookDetail.BookDetailResponse
import lars_lion.dev.o_harid.network.response.buyBook.BuyBookResponse
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

    fun getComment(bookId:String) = viewModelScope.launch {
        _comments.value = Event(UiState.Loading)
        try {
            repository.comments(bookId).catch { e ->
                _comments.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _comments.value = Event(UiState.Success(repos))
                else
                    _comments.value = Event(UiState.Error(repos.status.message))
            }
        } catch (e: Exception) {
            _comments.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }


    private val _bookDetail = MutableLiveData<Event<UiState<BookDetailResponse>>>()
    val bookDetail: LiveData<Event<UiState<BookDetailResponse>>> = _bookDetail

    fun getBookDetail(id: Int) = viewModelScope.launch {
        _bookDetail.value = Event(UiState.Loading)
        try {
            repository.bookDetail(id).catch { e ->
                _bookDetail.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _bookDetail.value = Event(UiState.Success(repos))
                else
                    _bookDetail.value = Event(UiState.Error(repos.status.message))
            }
        } catch (e: Exception) {
            _bookDetail.value = Event(UiState.Error(e.message ?: "message==null"))
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

    private val _buyBook = MutableLiveData<Event<UiState<BuyBookResponse>>>()
    val buyBook: LiveData<Event<UiState<BuyBookResponse>>> = _buyBook

    fun getBuyBook(id: Int) = viewModelScope.launch {
        _buyBook.value = Event(UiState.Loading)
        try {
            repository.buyBook(id).catch { e ->
                _buyBook.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _buyBook.value = Event(UiState.Success(repos))
                else
                    _buyBook.value = Event(UiState.Error(repos.status.message))
            }
        } catch (e: Exception) {
            _buyBook.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }

   private val _addComment = MutableLiveData<Event<UiState<AddCommentResponse>>>()
    val addComment: LiveData<Event<UiState<AddCommentResponse>>> = _addComment

    fun getAddComment(text:String, bookId: String, evaluate:String) = viewModelScope.launch {
        _addComment.value = Event(UiState.Loading)
        try {
            repository.addComment(text, bookId, evaluate).catch { e ->
                _addComment.value = Event(UiState.Error(e.message ?: "message==null"))
            }.collectLatest { repos ->
                if (repos.status.code == 200)
                    _addComment.value = Event(UiState.Success(repos))
                else
                    _addComment.value = Event(UiState.Error(repos.status.message))
            }
        } catch (e: Exception) {
            _addComment.value = Event(UiState.Error(e.message ?: "message==null"))
        }
    }


}
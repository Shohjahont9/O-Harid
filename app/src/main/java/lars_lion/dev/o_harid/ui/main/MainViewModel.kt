package lars_lion.dev.o_harid.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.bestSeller.BestSellerResponse
import lars_lion.dev.o_harid.network.response.bookType.BookTypeResponse
import lars_lion.dev.o_harid.network.response.nowadays.NowadaysResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel(){

    private val _bestSeller = MutableLiveData<Event<UiState<BestSellerResponse>>>()
    val bestSeller : LiveData<Event<UiState<BestSellerResponse>>> = _bestSeller

    fun getBestSeller(token:String)= viewModelScope.launch {
        _bestSeller.value = Event(UiState.Loading)
        try {
            repository.bestSeller(token).catch {e->
                Event(UiState.Error("main error ->${e.message}"))
            }.collect {response->
                if (response.status.code==200)
                    _bestSeller.value = Event(UiState.Success(response))
            }
        }catch (e:Exception){
            _bestSeller.value = Event(UiState.Error("main error ->${e.message}"))
        }
    }

    private val _nowadaysBook = MutableLiveData<Event<UiState<NowadaysResponse>>>()
    val nowadaysBook : LiveData<Event<UiState<NowadaysResponse>>> = _nowadaysBook

    fun getNowadaysBook(token:String)= viewModelScope.launch {
        _nowadaysBook.value = Event(UiState.Loading)
        try {
            repository.nowadaysBooks(token).catch {e->
                Event(UiState.Error("main error ->${e.message}"))
            }.collect {response->
                if (response.status.code==200)
                    _nowadaysBook.value = Event(UiState.Success(response))
            }
        }catch (e:Exception){
            _nowadaysBook.value = Event(UiState.Error("main error ->${e.message}"))
        }
    }

 private val _bookType = MutableLiveData<Event<UiState<BookTypeResponse>>>()
    val bookType : LiveData<Event<UiState<BookTypeResponse>>> = _bookType

    fun getBookType(token:String)= viewModelScope.launch {
        _bookType.value = Event(UiState.Loading)
        try {
            repository.janrBooks(token).catch {e->
                Event(UiState.Error("main error ->${e.message}"))
            }.collect {response->
                if (response.status.code==200)
                    _bookType.value = Event(UiState.Success(response))
            }
        }catch (e:Exception){
            _bookType.value = Event(UiState.Error("main error ->${e.message}"))
        }
    }



}
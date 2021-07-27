package lars_lion.dev.o_harid.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.network.response.bestSeller.BestSellerResponse
import lars_lion.dev.o_harid.utils.Event
import lars_lion.dev.o_harid.utils.UiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel(){

    private val _bestSeller = MutableLiveData<Event<UiState<BestSellerResponse>>>()
    val bestSeller : LiveData<Event<UiState<BestSellerResponse>>> = _bestSeller

    fun getBestSeller()= viewModelScope.launch {
        _bestSeller.value = Event(UiState.Loading)
        try {
            when(repository.bestSeller().code()){
                200 -> Event(UiState.Success(repository.bestSeller().body()!!))
            }
        }catch (e:Exception){
            _bestSeller.value = Event(UiState.Error(e.message.toString()))
        }
    }


}
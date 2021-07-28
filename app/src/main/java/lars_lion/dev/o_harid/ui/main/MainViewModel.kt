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

    private val _bestSeller = MutableLiveData<UiState<BestSellerResponse>>()
    val bestSeller : LiveData<UiState<BestSellerResponse>> = _bestSeller

    fun getBestSeller(response: BestSellerResponse)= viewModelScope.launch {
        _bestSeller.value = UiState.Loading
        try {
            when(response.status.code){
                200 -> UiState.Success(response)
            }
        }catch (e:Exception){
            _bestSeller.value = UiState.Error("main error ->${e.message}")
        }
    }

    suspend fun fetchBestSeller(token:String) = repository.bestSeller(token)


}
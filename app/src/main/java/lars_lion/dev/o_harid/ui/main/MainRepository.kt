package lars_lion.dev.o_harid.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun bestSeller(token:String) = flow {
        emit(apiService.bestSeller(token, 10))
    }.flowOn(Dispatchers.IO)

}
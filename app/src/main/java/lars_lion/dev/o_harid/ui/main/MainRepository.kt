package lars_lion.dev.o_harid.ui.main

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import javax.inject.Inject

@ViewModelScoped
class MainRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun bestSeller(token: String) = flow {
        emit(apiService.bestSeller(token, 10))
    }.flowOn(Dispatchers.IO)

    suspend fun nowadaysBooks(token: String) = flow {
        emit(apiService.nowadaysBooks(token, 10))
    }.flowOn(Dispatchers.IO)

    suspend fun janrBooks(token: String) = flow {
        emit(apiService.bookType(token))
    }.flowOn(Dispatchers.IO)


    suspend fun searchBooks(token: String, name: String) = flow {
        emit(apiService.search(token, name))
    }.flowOn(Dispatchers.IO)


}
package lars_lion.dev.o_harid.ui.registration.reg

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import javax.inject.Inject

class RegisstrationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun register(body: String) = flow {
        emit(apiService.register(body))
    }.flowOn(Dispatchers.IO)
}
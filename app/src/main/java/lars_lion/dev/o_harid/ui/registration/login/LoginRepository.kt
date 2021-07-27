package lars_lion.dev.o_harid.ui.registration.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(body: String) = flow {
        emit(apiService.login(body))
    }.flowOn(Dispatchers.IO)
}
package lars_lion.dev.o_harid.ui.registration.login

import lars_lion.dev.o_harid.network.ApiService
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(body:String) = apiService.login(body)
}
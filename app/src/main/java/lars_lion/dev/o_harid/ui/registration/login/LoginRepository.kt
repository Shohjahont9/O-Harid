package lars_lion.dev.o_harid.ui.registration.login

import lars_lion.dev.o_harid.network.ApiService

class LoginRepository(
    private val apiService: ApiService
) {
    suspend fun login(body:String) = apiService.login(body)
}
package lars_lion.dev.o_harid.ui.registration.reg

import lars_lion.dev.o_harid.network.ApiService
import javax.inject.Inject

class RegisstrationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun register(body:String) = apiService.register(body)
}
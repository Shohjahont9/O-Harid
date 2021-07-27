package lars_lion.dev.o_harid.ui.main

import lars_lion.dev.o_harid.network.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun bestSeller(token:String) = apiService.bestSeller(token, 0)

}
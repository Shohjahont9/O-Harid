package lars_lion.dev.o_harid.ui.main

import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs:PreferencesManager
) {
    suspend fun bestSeller() = apiService.bestSeller(prefs.token, 0)

}
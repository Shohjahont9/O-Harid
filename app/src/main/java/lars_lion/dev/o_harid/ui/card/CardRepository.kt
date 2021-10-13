package lars_lion.dev.o_harid.ui.card

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) {
    suspend fun createCard(body: String) = flow {
        emit(apiService.createCard("Bearer ${prefs.token}", body))
    }.flowOn(Dispatchers.IO)

}
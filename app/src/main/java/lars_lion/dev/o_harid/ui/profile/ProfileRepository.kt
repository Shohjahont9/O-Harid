package lars_lion.dev.o_harid.ui.profile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) {
    suspend fun getUserMoney() = flow {
        emit(apiService.getUserMoney("Bearer ${prefs.token}"))
    }.flowOn(Dispatchers.IO)

}
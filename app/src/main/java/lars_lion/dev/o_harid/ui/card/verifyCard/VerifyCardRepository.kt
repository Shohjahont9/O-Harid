package lars_lion.dev.o_harid.ui.card.verifyCard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject

class VerifyCardRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) {

    suspend fun getCode(code: String) = flow {
        emit(apiService.getVerifyCode(prefs.token, code))
    }.flowOn(Dispatchers.IO)
}